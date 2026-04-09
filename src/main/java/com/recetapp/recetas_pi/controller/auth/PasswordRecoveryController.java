package com.recetapp.recetas_pi.controller.auth;

import com.recetapp.recetas_pi.dto.auth.PasswordForgotRequest;
import com.recetapp.recetas_pi.dto.auth.PasswordResetRequest;
import com.recetapp.recetas_pi.dto.auth.PasswordTokenValidationResponse;
import com.recetapp.recetas_pi.service.PasswordRecoveryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/password")
@Validated
public class PasswordRecoveryController {

    @Autowired
    private PasswordRecoveryService passwordRecoveryService;

    @PostMapping("/forgot")
    public ResponseEntity<?> forgot(@Valid @RequestBody PasswordForgotRequest req) {
        try {
            passwordRecoveryService.solicitarRecuperacion(req.getCorreo());
            return ResponseEntity.ok(Map.of("mensaje", "Si el correo existe, te hemos enviado instrucciones"));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/validate/{token}")
    public ResponseEntity<PasswordTokenValidationResponse> validate(@PathVariable String token) {
        PasswordTokenValidationResponse response = passwordRecoveryService.validarToken(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset")
    public ResponseEntity<?> reset(@Valid @RequestBody PasswordResetRequest req) {
        try {
            passwordRecoveryService.resetearPassword(req.getToken(), req.getNuevaPassword());
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("Token invalido".equals(msg) || "Token expirado".equals(msg) || "Token ya utilizado".equals(msg)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }
}
