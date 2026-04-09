package com.recetapp.recetas_pi.service;

import com.recetapp.recetas_pi.dto.auth.PasswordTokenValidationResponse;
import com.recetapp.recetas_pi.model.RecuperacionPassword;
import com.recetapp.recetas_pi.model.Usuario;
import com.recetapp.recetas_pi.repository.RecuperacionPasswordRepository;
import com.recetapp.recetas_pi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordRecoveryService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RecuperacionPasswordRepository recuperacionPasswordRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.password-recovery.token-expiration-minutes:30}")
    private long tokenExpirationMinutes;

    @Value("${app.password-recovery.deep-link-base:recetapp://reset-password}")
    private String deepLinkBase;

    @Value("${app.password-recovery.from-mail:}")
    private String fromMail;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public void solicitarRecuperacion(String correo) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            // No revelamos si el correo existe para evitar enumeracion de usuarios.
            return;
        }

        Usuario usuario = usuarioOpt.get();
        recuperacionPasswordRepository.deleteByUsuarioIdAndUsadoFalse(usuario.getId());

        String token = UUID.randomUUID().toString() + UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();

        RecuperacionPassword recuperacion = new RecuperacionPassword();
        recuperacion.setUsuario(usuario);
        recuperacion.setToken(token);
        recuperacion.setFechaCreacion(ahora);
        recuperacion.setFechaExpiracion(ahora.plusMinutes(tokenExpirationMinutes));
        recuperacion.setUsado(false);
        recuperacionPasswordRepository.save(recuperacion);

        String link = construirDeepLink(token);
        enviarEmailRecuperacion(usuario.getCorreo(), link);
    }

    public PasswordTokenValidationResponse validarToken(String token) {
        Optional<RecuperacionPassword> recOpt = recuperacionPasswordRepository.findByToken(token);
        if (recOpt.isEmpty()) {
            return new PasswordTokenValidationResponse(false, "Token invalido");
        }

        RecuperacionPassword rec = recOpt.get();
        if (Boolean.TRUE.equals(rec.getUsado())) {
            return new PasswordTokenValidationResponse(false, "Token ya utilizado");
        }

        if (rec.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            return new PasswordTokenValidationResponse(false, "Token expirado");
        }

        return new PasswordTokenValidationResponse(true, "Token valido");
    }

    @Transactional
    public void resetearPassword(String token, String nuevaPassword) {
        RecuperacionPassword rec = recuperacionPasswordRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalido"));

        if (Boolean.TRUE.equals(rec.getUsado())) {
            throw new RuntimeException("Token ya utilizado");
        }

        if (rec.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        Usuario usuario = rec.getUsuario();
        if (passwordEncoder.matches(nuevaPassword, usuario.getPassword())) {
            throw new RuntimeException("La nueva contraseña debe ser diferente");
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        rec.setUsado(true);
        recuperacionPasswordRepository.save(rec);
    }

    private String construirDeepLink(String token) {
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String separator = deepLinkBase.contains("?") ? "&" : "?";
        return deepLinkBase + separator + "token=" + encodedToken;
    }

    private void enviarEmailRecuperacion(String destino, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (fromMail != null && !fromMail.isBlank()) {
            message.setFrom(fromMail);
        }
        message.setTo(destino);
        message.setSubject("Recuperacion de contraseña - RecetApp");
        message.setText(
                "Hola,\n\n" +
                "Has solicitado recuperar tu contraseña.\n" +
                "Abre este enlace desde tu app para continuar:\n\n" +
                link + "\n\n" +
                "Este enlace expira en " + tokenExpirationMinutes + " minutos.\n" +
                "Si no solicitaste este cambio, ignora este correo."
        );
        mailSender.send(message);
    }
}
