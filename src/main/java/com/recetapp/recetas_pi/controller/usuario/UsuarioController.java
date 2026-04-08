package com.recetapp.recetas_pi.controller.usuario;

import com.recetapp.recetas_pi.dto.favorito.FavoritoResponse;
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
import com.recetapp.recetas_pi.model.Favorito;
import com.recetapp.recetas_pi.service.UsuarioService;
import com.recetapp.recetas_pi.repository.FavoritoRepository;
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
/**
 * Controlador REST para la gestión de usuarios.
 * 
 * <p>
 * Cada endpoint está documentado para que entendáis claramente
 * cómo consumirlo, qué datos espera y qué devuelve. Se incluyen ejemplos de uso.
 * </p>
 */
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FavoritoRepository favoritoRepository;

    /**
     * Registro de usuario
     * POST /api/usuarios/register
     *
     * Espera un JSON con los datos del usuario y una lista de alergias (opcional).
     * Ejemplo de request:
     * {
     *   "nombre": "Juan",
     *   "apellidos": "Pérez",
     *   "correo": "juan@mail.com",
     *   "password": "123456",
     *   "alergias": ["Gluten", "Lácteos"]
     * }
     *
     * Responde con el usuario creado (sin password) o error.
     */
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

    /**
     * Login de usuario (JSON)
     * POST /api/usuarios/login
     * Content-Type: application/json
     *
     * Espera un JSON:
     * {
     *   "correo": "juan@mail.com",
     *   "password": "123456"
     * }
     *
     * Responde con:
     * {
     *   "token": "...JWT...",
     *   "usuario": { ...datos usuario... }
     * }
     *
     * Si las credenciales son incorrectas, responde 401.
     */
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

    /**
     * Login de usuario (formulario)
     * POST /api/usuarios/login
     * Content-Type: application/x-www-form-urlencoded
     *
     * Ejemplo de request:
     * correo=juan@mail.com&password=123456
     *
     * Responde igual que el login JSON. Útil para formularios HTML.
     */
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

    /**
     * Obtener usuario por ID
     * GET /api/usuarios/{id}
     *
     * Devuelve los datos del usuario con ese ID. Si no existe, responde 404.
     * Ejemplo de respuesta:
     * {
     *   "id": 1,
     *   "nombre": "Juan",
     *   ...
     * }
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.getUsuarioById(id);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(toResponse(usuario.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
    }

    /**
     * Actualizar perfil del usuario autenticado
     * PUT /api/usuarios/me
     *
     * Requiere JWT en el header Authorization.
     * Body (JSON):
     * {
     *   "nombre": "Nuevo nombre",
     *   "apellidos": "Nuevos apellidos",
     *   "correo": "nuevo@mail.com"
     * }
     *
     * Devuelve el usuario actualizado o error.
     */
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

    /**
     * Reemplazar todas las alergias del usuario autenticado
     * PUT /api/usuarios/me/alergias
     *
     * Requiere JWT. Body (JSON):
     * {
     *   "alergias": ["Gluten", "Lácteos"]
     * }
     *
     * Sobrescribe la lista de alergias del usuario.
     * Devuelve el usuario actualizado.
     */
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

    /**
     * Agregar alergias al usuario autenticado (sin eliminar las anteriores)
     * POST /api/usuarios/me/alergias
     *
     * Requiere JWT. Body (JSON):
     * {
     *   "alergias": ["Mariscos"]
     * }
     *
     * Añade las alergias indicadas a las ya existentes.
     * Devuelve el usuario actualizado.
     */
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

    /**
     * Eliminar una alergia específica del usuario autenticado
     * DELETE /api/usuarios/me/alergias/{nombre}
     *
     * Requiere JWT. El parámetro {nombre} es el nombre de la alergia a eliminar.
     * Ejemplo: DELETE /api/usuarios/me/alergias/Gluten
     *
     * Devuelve el usuario actualizado.
     */
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

    /**
     * Cambiar la contraseña del usuario autenticado
     * PATCH /api/usuarios/me/password
     *
     * Requiere JWT. Body (JSON):
     * {
     *   "passwordActual": "123456",
     *   "passwordNueva": "nuevaPassword"
     * }
     *
     * Devuelve mensaje de éxito o error.
     */
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

    /**
     * Obtener los datos del usuario autenticado (según JWT)
     * GET /api/usuarios/me
     *
     * Requiere JWT. Devuelve los datos del usuario autenticado.
     * Si el token no es válido, responde 401.
     */
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

        List<Favorito> favoritos = favoritoRepository.findByUsuarioCorreoOrderByFechaAgregadoDesc(u.getCorreo());
        List<FavoritoResponse> favoritosResponse = new ArrayList<>();
        for (Favorito favorito : favoritos) {
            FavoritoResponse fr = new FavoritoResponse();
            if (favorito.getReceta() != null) {
                fr.setRecetaId(favorito.getReceta().getId());
                fr.setTitulo(favorito.getReceta().getTitulo());
                fr.setImagenUrl(favorito.getReceta().getImagenUrl());
            }
            fr.setFechaAgregado(favorito.getFechaAgregado());
            favoritosResponse.add(fr);
        }
        r.setFavoritos(favoritosResponse);

        r.setFechaCreacion(u.getFechaCreacion());
        return r;
    }
}
