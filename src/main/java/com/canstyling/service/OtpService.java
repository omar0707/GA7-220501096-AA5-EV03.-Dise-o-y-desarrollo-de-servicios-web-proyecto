package com.canstyling.service;

import com.canstyling.dao.OtpDAO;
import com.canstyling.dao.UsuarioDAO;
import com.canstyling.dao.impl.OtpDAOImpl;
import com.canstyling.dao.impl.UsuarioDAOImpl;
import com.canstyling.model.CodigoOTP;
import com.canstyling.model.Usuario;
import com.canstyling.security.SecurityUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OtpService {

    private final OtpDAO otpDAO = new OtpDAOImpl();
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    public Map<String, Object> solicitarOTP(String correo) {
        Map<String, Object> respuesta = new HashMap<>();

        if (correo == null || !correo.contains("@")) {
            respuesta.put("success", false);
            respuesta.put("message", "Correo electrónico inválido.");
            return respuesta;
        }

        String codigoGenerado = SecurityUtils.generarCodigoOTP();
        String codigoHash = SecurityUtils.hashPassword(codigoGenerado);
        Timestamp expiraEn = Timestamp.from(Instant.now().plus(5, ChronoUnit.MINUTES));

        CodigoOTP otp = new CodigoOTP(correo, codigoHash, expiraEn);
        boolean guardado = otpDAO.guardarOTP(otp);

        if (guardado) {
            respuesta.put("success", true);
            respuesta.put("message", "Código OTP de 6 dígitos enviado exitosamente.");
            respuesta.put("codigoSimulado", codigoGenerado); // Retornado para pruebas locales
            respuesta.put("expiraEnMinutos", 5);
        } else {
            respuesta.put("success", false);
            respuesta.put("message", "Error al procesar la solicitud de OTP.");
        }
        return respuesta;
    }

    public Map<String, Object> verificarOTP(String correo, String codigoIngresado) {
        Map<String, Object> respuesta = new HashMap<>();

        Optional<CodigoOTP> otpOpt = otpDAO.buscarOTPValido(correo);
        if (otpOpt.isEmpty()) {
            respuesta.put("success", false);
            respuesta.put("message", "Código OTP expirado o no encontrado.");
            return respuesta;
        }

        CodigoOTP otp = otpOpt.get();
        if (otp.getIntentos() >= 3) {
            otpDAO.marcarComoUsado(otp.getIdOtp());
            respuesta.put("success", false);
            respuesta.put("message", "Límite de intentos superado. Solicite un nuevo código.");
            return respuesta;
        }

        String hashIngresado = SecurityUtils.hashPassword(codigoIngresado);
        if (hashIngresado.equals(otp.getCodigoHash())) {
            otpDAO.marcarComoUsado(otp.getIdOtp());
            Optional<Usuario> userOpt = usuarioDAO.buscarPorCorreo(correo);
            respuesta.put("success", true);
            respuesta.put("message", "Autenticación OTP exitosa.");
            respuesta.put("usuario", userOpt.orElse(null));
            respuesta.put("token", "JWT_OTP_" + SecurityUtils.generarTokenAntiIDOR());
        } else {
            otpDAO.incrementarIntentos(otp.getIdOtp());
            respuesta.put("success", false);
            respuesta.put("message", "Código OTP incorrecto. Intento " + (otp.getIntentos() + 1) + " de 3.");
        }
        return respuesta;
    }
}
