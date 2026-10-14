package com.canstyling.controller;

import com.canstyling.dto.ApiResponse;
import com.canstyling.dto.LoginRequest;
import com.canstyling.dto.OtpRequest;
import com.canstyling.model.Usuario;
import com.canstyling.service.AuthService;
import com.canstyling.service.OtpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService = new AuthService();
    private final OtpService otpService = new OtpService();

    /**
     * Requerimiento Específico SENA:
     * El servicio recibirá un usuario y una contraseña, si la autenticación es correcta
     * saldrá un mensaje de 'Autenticación satisfactoria', en caso contrario devolverá
     * 'Error en la autenticación'.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody LoginRequest req) {
        Map<String, Object> authResult = authService.autenticar(req.getCorreo(), req.getPassword());
        boolean success = (boolean) authResult.get("success");

        if (success) {
            return ResponseEntity.ok(ApiResponse.ok("Autenticación satisfactoria", authResult));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Error en la autenticación", null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Usuario>> register(@RequestBody Usuario usuario) {
        boolean ok = authService.registrarUsuario(usuario);
        if (ok) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Usuario registrado exitosamente en CanStyling", usuario));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("El correo ya se encuentra registrado"));
        }
    }

    @PostMapping("/otp/request")
    public ResponseEntity<ApiResponse<Map<String, Object>>> requestOtp(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        Map<String, Object> res = otpService.solicitarOTP(correo);
        if ((boolean) res.get("success")) {
            return ResponseEntity.ok(ApiResponse.ok((String) res.get("message"), res));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error((String) res.get("message")));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyOtp(@RequestBody OtpRequest req) {
        Map<String, Object> res = otpService.verificarOTP(req.getCorreo(), req.getCodigo());
        if ((boolean) res.get("success")) {
            return ResponseEntity.ok(ApiResponse.ok("Autenticación OTP satisfactoria", res));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error((String) res.get("message")));
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<ApiResponse<String>> recuperarPassword(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        String password = body.get("password");
        boolean ok = authService.actualizarPassword(correo, password);
        if (ok) {
            return ResponseEntity.ok(ApiResponse.ok("Contraseña actualizada exitosamente en base de datos", correo));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("No se pudo actualizar la contraseña"));
    }
}
