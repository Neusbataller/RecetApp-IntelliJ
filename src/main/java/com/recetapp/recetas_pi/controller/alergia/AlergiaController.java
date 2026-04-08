package com.recetapp.recetas_pi.controller.alergia;

import com.recetapp.recetas_pi.dto.alergia.AlergiaCreateRequest;
import com.recetapp.recetas_pi.dto.alergia.AlergiaResponse;
import com.recetapp.recetas_pi.model.Alergia;
import com.recetapp.recetas_pi.model.Usuario;
import com.recetapp.recetas_pi.service.AlergiaService;
import com.recetapp.recetas_pi.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/alergias")
@Validated
/**
 * Controlador REST para la gestión de alergias.
 *
 * <p>
 * Cada endpoint está documentado para que entendáis claramente
 * cómo consumirlo, qué datos espera y qué devuelve. Se incluyen ejemplos de uso.
 * </p>
 */
public class AlergiaController {

    @Autowired
    private AlergiaService alergiaService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtener todas las alergias
     * GET /api/alergias
     *
     * Devuelve un array de alergias:
     * [
     *   { "id": 1, "nombre": "Gluten" },
     *   { "id": 2, "nombre": "Lácteos" }
     * ]
     */
    @GetMapping
    public ResponseEntity<List<AlergiaResponse>> getAll() {
        List<Alergia> alergias = alergiaService.getAll();
        List<AlergiaResponse> response = new ArrayList<>();
        for (Alergia a : alergias) {
            response.add(toResponse(a));
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener alergias del usuario autenticado.
     * GET /api/alergias/me
     *
     * Requiere JWT y devuelve solo las alergias del usuario logeado.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMine() {
        String correoAuth = getCorreoAutenticado();
        if (correoAuth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        Optional<Usuario> usuario = usuarioService.getUsuarioByCorreo(correoAuth);
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        List<AlergiaResponse> response = new ArrayList<>();
        if (usuario.get().getAlergias() != null) {
            for (Alergia a : usuario.get().getAlergias()) {
                response.add(toResponse(a));
            }
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener una alergia por su ID
     * GET /api/alergias/{id}
     *
     * Devuelve la alergia si existe:
     * { "id": 1, "nombre": "Gluten" }
     *
     * Si no existe, responde 404 y mensaje "Alergia no encontrada".
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Alergia> alergia = alergiaService.getById(id);
        if (alergia.isPresent()) {
            return ResponseEntity.ok(toResponse(alergia.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alergia no encontrada");
    }

    /**
     * Crear una nueva alergia
     * POST /api/alergias
     *
     * Body (JSON):
     * { "nombre": "Frutos secos" }
     *
     * Devuelve la alergia creada:
     * { "id": 3, "nombre": "Frutos secos" }
     *
     * Si el nombre ya existe o hay error, responde 400 y mensaje de error.
     */
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody AlergiaCreateRequest req) {
        try {
            Alergia created = alergiaService.create(req.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    /**
     * Actualizar el nombre de una alergia
     * PUT /api/alergias/{id}
     *
     * Body (JSON):
     * { "nombre": "Nuevo nombre" }
     *
     * Devuelve la alergia actualizada:
     * { "id": 1, "nombre": "Nuevo nombre" }
     *
     * Si no existe, responde 404. Si hay error, responde 400.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody AlergiaCreateRequest req) {
        try {
            Alergia updated = alergiaService.update(id, req.getNombre());
            return ResponseEntity.ok(toResponse(updated));
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("Alergia no encontrada".equals(msg)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }

    /**
     * Eliminar una alergia por su ID
     * DELETE /api/alergias/{id}
     *
     * Devuelve mensaje de éxito: "Alergia eliminada correctamente"
     * Si no existe, responde 404 y mensaje de error.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            alergiaService.deleteById(id);
            return ResponseEntity.ok("Alergia eliminada correctamente");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
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

    private AlergiaResponse toResponse(Alergia a) {
        AlergiaResponse response = new AlergiaResponse();
        response.setId(a.getId());
        response.setNombre(a.getNombre());
        return response;
    }
}