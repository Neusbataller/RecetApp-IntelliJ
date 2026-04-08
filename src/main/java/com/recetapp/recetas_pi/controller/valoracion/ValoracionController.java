package com.recetapp.recetas_pi.controller.valoracion;

import com.recetapp.recetas_pi.dto.valoracion.ValoracionCreateRequest;
import com.recetapp.recetas_pi.dto.valoracion.ValoracionResponse;
import com.recetapp.recetas_pi.dto.valoracion.ValoracionStatsResponse;
import com.recetapp.recetas_pi.dto.valoracion.ValoracionUpdateRequest;
import com.recetapp.recetas_pi.model.Valoracion;
import com.recetapp.recetas_pi.service.ValoracionService;
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
@RequestMapping("/api/recetas/{recetaId}/valoraciones")
@Validated
/**
 * Controlador REST para valoraciones de recetas.
 *
 * <p>
 * Este controlador está anidado en receta para reflejar la regla de negocio:
 * toda valoración pertenece a una receta concreta. Aquí frontend/backend pueden
 * listar valoraciones, consultar métricas y gestionar la valoración del usuario logeado.
 * </p>
 */
public class ValoracionController {

    @Autowired
    private ValoracionService valoracionService;

    /**
     * Listar valoraciones de una receta (público).
     * GET /api/recetas/{recetaId}/valoraciones
     *
     * Devuelve un array con todas las valoraciones de la receta,
     * ordenadas por fecha de creación descendente.
     *
     * Si la receta no existe, responde 404.
     */
    @GetMapping
    public ResponseEntity<?> getByReceta(@PathVariable Long recetaId) {
        try {
            return ResponseEntity.ok(toResponseList(valoracionService.getByReceta(recetaId)));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    /**
     * Obtener estadísticas de valoraciones de una receta (público).
     * GET /api/recetas/{recetaId}/valoraciones/stats
     *
     * Devuelve:
     * - recetaId
     * - mediaPuntuacion
     * - totalValoraciones
     *
     * Si la receta no existe, responde 404.
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@PathVariable Long recetaId) {
        try {
            ValoracionStatsResponse stats = valoracionService.getStats(recetaId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    /**
     * Obtener mi valoración de una receta (requiere JWT).
     * GET /api/recetas/{recetaId}/valoraciones/mia
     *
     * Útil para saber si el usuario ya valoró la receta y precargar
     * la UI de edición (estrellas/comentario).
     *
     * Respuestas:
     * - 200: devuelve la valoración del usuario autenticado.
     * - 401: no autenticado.
     * - 404: la receta no existe o el usuario aún no ha valorado.
     */
    @GetMapping("/mia")
    public ResponseEntity<?> getMine(@PathVariable Long recetaId) {
        String correo = getCorreoAutenticado();
        if (correo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            Optional<Valoracion> mine = valoracionService.getMine(correo, recetaId);
            if (mine.isPresent()) {
                return ResponseEntity.ok(toResponse(mine.get()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Valoracion no encontrada");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    /**
     * Crear mi valoración para una receta (requiere JWT).
     * POST /api/recetas/{recetaId}/valoraciones
     *
     * Body JSON:
     * {
     *   "puntuacion": 5,
     *   "comentario": "Muy buena receta"
     * }
     *
     * Respuestas:
     * - 201: valoración creada.
     * - 401: no autenticado.
     * - 404: receta no encontrada.
     * - 409: el usuario ya tenía una valoración para esta receta.
     */
    @PostMapping
    public ResponseEntity<?> create(@PathVariable Long recetaId, @Valid @RequestBody ValoracionCreateRequest req) {
        String correo = getCorreoAutenticado();
        if (correo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            Valoracion created = valoracionService.create(correo, recetaId, req.getPuntuacion(), req.getComentario());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("Receta no encontrada".equals(msg)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            if ("Ya has valorado esta receta".equals(msg)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }

    /**
     * Actualizar mi valoración de una receta (requiere JWT).
     * PUT /api/recetas/{recetaId}/valoraciones/mia
     *
     * Body JSON:
     * {
     *   "puntuacion": 4,
     *   "comentario": "La he repetido con cambios"
     * }
     *
     * Respuestas:
     * - 200: valoración actualizada.
     * - 401: no autenticado.
     * - 404: no existe valoración previa del usuario para esa receta.
     */
    @PutMapping("/mia")
    public ResponseEntity<?> updateMine(@PathVariable Long recetaId, @Valid @RequestBody ValoracionUpdateRequest req) {
        String correo = getCorreoAutenticado();
        if (correo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            Valoracion updated = valoracionService.updateMine(correo, recetaId, req.getPuntuacion(), req.getComentario());
            return ResponseEntity.ok(toResponse(updated));
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("Valoracion no encontrada".equals(msg)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }

    /**
     * Eliminar mi valoración de una receta (requiere JWT).
     * DELETE /api/recetas/{recetaId}/valoraciones/mia
     *
     * Respuestas:
     * - 200: valoración eliminada correctamente.
     * - 401: no autenticado.
     * - 404: no existía valoración del usuario en esa receta.
     */
    @DeleteMapping("/mia")
    public ResponseEntity<?> deleteMine(@PathVariable Long recetaId) {
        String correo = getCorreoAutenticado();
        if (correo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            valoracionService.deleteMine(correo, recetaId);
            return ResponseEntity.ok("Valoracion eliminada correctamente");
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("Valoracion no encontrada".equals(msg)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
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

    private List<ValoracionResponse> toResponseList(List<Valoracion> valoraciones) {
        List<ValoracionResponse> response = new ArrayList<>();
        for (Valoracion valoracion : valoraciones) {
            response.add(toResponse(valoracion));
        }
        return response;
    }

    private ValoracionResponse toResponse(Valoracion v) {
        ValoracionResponse response = new ValoracionResponse();
        response.setId(v.getId());
        if (v.getUsuario() != null) {
            response.setUsuarioId(v.getUsuario().getId());
        }
        if (v.getReceta() != null) {
            response.setRecetaId(v.getReceta().getId());
        }
        response.setPuntuacion(v.getPuntuacion());
        response.setComentario(v.getComentario());
        response.setFechaCreacion(v.getFechaCreacion());
        return response;
    }
}