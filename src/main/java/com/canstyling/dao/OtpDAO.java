package com.canstyling.dao;

import com.canstyling.model.CodigoOTP;
import java.util.Optional;

public interface OtpDAO {
    boolean guardarOTP(CodigoOTP otp);
    Optional<CodigoOTP> buscarOTPValido(String correo);
    boolean marcarComoUsado(int idOtp);
    boolean incrementarIntentos(int idOtp);
}
