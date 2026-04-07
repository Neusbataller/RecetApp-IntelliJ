package com.recetapp.recetas_pi.controller.usuario;

import com.recetapp.recetas_pi.dto.usuario.UsuarioRegistroRequest;
import com.recetapp.recetas_pi.dto.usuario.UsuarioResponse;
import com.recetapp.recetas_pi.dto.usuario.UsuarioLoginRequest;
import com.recetapp.recetas_pi.dto.usuario.UsuarioPerfilUpdateRequest;
import com.recetapp.recetas_pi.dto.usuario.UsuarioAlergiasUpdateRequest;
import com.recetapp.recetas_pi.dto.usuario.UsuarioPasswordUpdateRequest;
import com.recetapp.recetas_pi.dto.usuario.AuthResponse;
import com.recetapp.recetas_pi.security.JwtUtil;
import com.recetapp.recetas_pi.model.Usuario;
import com.recetapp.recetas_pi.model.Alergia;
import com.recetapp.recetas_pi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@Validated
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UsuarioRegistroRequest req) {
        try {
            Usuario usuario = new Usuario();
            usuario.setNombre(req.getNombre());
            usuario.setApellidos(req.getApellidos());
            usuario.setCorreo(req.getCorreo());
            usuario.setPassword(req.getPassword());
            usuario.setFechaCreacion(LocalDateTime.now());

            Usuario saved = usuarioService.registroConAlergias(usuario, req.getAlergias());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    // POST /login expects a JSON body: { "correo": "...", "password": "..." }
    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<?> loginJson(@Valid @RequestBody UsuarioLoginRequest req) {
        Optional<Usuario> usuarioOpt = usuarioService.login(req.getCorreo(), req.getPassword());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String token = jwtUtil.generateToken(usuario);
            AuthResponse auth = new AuthResponse(token, toResponse(usuario));
            return ResponseEntity.status(HttpStatus.OK).body(auth);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
    }

    // Also accept form-url-encoded POST for convenience (e.g., HTML forms)
    @PostMapping(value = "/login", consumes = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> loginForm(@RequestParam String correo, @RequestParam String password) {
        Optional<Usuario> usuarioOpt = usuarioService.login(correo, password);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String token = jwtUtil.generateToken(usuario);
            AuthResponse auth = new AuthResponse(token, toResponse(usuario));
            return ResponseEntity.status(HttpStatus.OK).body(auth);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
    }

    // (GET /login removed — use POST /login with JSON or form data)

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.getUsuarioById(id);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(toResponse(usuario.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMe(@Valid @RequestBody UsuarioPerfilUpdateRequest req) {
        String correoAuth = getCorreoAutenticado();
        if (correoAuth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            Usuario updated = usuarioService.actualizarPerfilPorCorreo(
                    correoAuth,
                    req.getNombre(),
                    req.getApellidos(),
                    req.getCorreo());
            return ResponseEntity.ok(toResponse(updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/me/alergias")
    public ResponseEntity<?> updateMisAlergias(@Valid @RequestBody UsuarioAlergiasUpdateRequest req) {
        String correoAuth = getCorreoAutenticado();
        if (correoAuth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            Usuario updated = usuarioService.actualizarAlergiasPorCorreo(correoAuth, req.getAlergias());
            return ResponseEntity.ok(toResponse(updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/me/alergias")
    public ResponseEntity<?> addMisAlergias(@Valid @RequestBody UsuarioAlergiasUpdateRequest req) {
        String correoAuth = getCorreoAutenticado();
        if (correoAuth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            Usuario updated = usuarioService.agregarAlergiasPorCorreo(correoAuth, req.getAlergias());
            return ResponseEntity.ok(toResponse(updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/me/alergias/{nombre}")
    public ResponseEntity<?> deleteMiAlergia(@PathVariable String nombre) {
        String correoAuth = getCorreoAutenticado();
        if (correoAuth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            Usuario updated = usuarioService.eliminarAlergiaPorCorreo(correoAuth, nombre);
            return ResponseEntity.ok(toResponse(updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PatchMapping("/me/password")
    public ResponseEntity<?> updateMiPassword(@Valid @RequestBody UsuarioPasswordUpdateRequest req) {
        String correoAuth = getCorreoAutenticado();
        if (correoAuth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            usuarioService.actualizarPasswordPorCorreo(correoAuth, req.getPasswordActual(), req.getPasswordNueva());
            return ResponseEntity.ok("Contraseña actualizada correctamente");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    // Returns the currently authenticated user (based on JWT)
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        String correoAuth = getCorreoAutenticado();
        if (correoAuth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        Optional<Usuario> usuario = usuarioService.getUsuarioByCorreo(correoAuth);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(toResponse(usuario.get()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
    }

    private String getCorreoAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof String correo && !"anonymousUser".equals(correo)) {
            return correo;
        }
        return null;
    }

    private UsuarioResponse toResponse(Usuario u) {
        UsuarioResponse r = new UsuarioResponse();
        r.setId(u.getId());
        r.setNombre(u.getNombre());
        r.setApellidos(u.getApellidos());
        r.setCorreo(u.getCorreo());
        if (u.getAlergias() != null) {
            List<String> nombres = new ArrayList<>();
            for (Alergia a : u.getAlergias()) {
                nombres.add(a.getNombre());
            }
            r.setAlergias(nombres);
        }
        r.setFechaCreacion(u.getFechaCreacion());
        return r;
    }
}
