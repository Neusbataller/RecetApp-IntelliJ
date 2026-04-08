package com.recetapp.recetas_pi.controller.favorito;

import com.recetapp.recetas_pi.dto.favorito.FavoritoResponse;
import com.recetapp.recetas_pi.dto.favorito.FavoritoToggleResponse;
import com.recetapp.recetas_pi.model.Favorito;
import com.recetapp.recetas_pi.service.FavoritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
@Validated
/**
 * Controlador REST para favoritos.
 *
 * <p>
 * Pensado para frontend: permite listar, consultar estado,
 * agregar, eliminar y hacer toggle (corazon) en una receta.
 * </p>
 */
public class FavoritoController {

    @Autowired
    private FavoritoService favoritoService;

    /**
     * Obtener favoritos del usuario autenticado.
     * GET /api/favoritos/getMine
     */
    @GetMapping("/getMine")
    public ResponseEntity<?> getMine() {
        String correo = getCorreoAutenticado();
        if (correo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        return ResponseEntity.ok(toResponseList(favoritoService.getFavoritosByCorreo(correo)));
    }

    /**
     * Saber si una receta esta en favoritos del usuario autenticado.
     * GET /api/favoritos/isFavorite/{recetaId}
     */
    @GetMapping("/isFavorite/{recetaId}")
    public ResponseEntity<?> isFavorite(@PathVariable Long recetaId) {
        String correo = getCorreoAutenticado();
        if (correo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        boolean favorito = favoritoService.isFavorito(correo, recetaId);
        return ResponseEntity.ok(favorito);
    }

    /**
     * Contar favoritos de una receta.
     * GET /api/favoritos/countByReceta/{recetaId}
     */
    @GetMapping("/countByReceta/{recetaId}")
    public ResponseEntity<Long> countByReceta(@PathVariable Long recetaId) {
        return ResponseEntity.ok(favoritoService.countByReceta(recetaId));
    }

    /**
     * Agregar receta a favoritos.
     * POST /api/favoritos/add/{recetaId}
     */
    @PostMapping("/add/{recetaId}")
    public ResponseEntity<?> add(@PathVariable Long recetaId) {
        String correo = getCorreoAutenticado();
        if (correo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            Favorito favorito = favoritoService.addFavorito(correo, recetaId);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(favorito));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    /**
     * Eliminar receta de favoritos.
     * DELETE /api/favoritos/remove/{recetaId}
     */
    @DeleteMapping("/remove/{recetaId}")
    public ResponseEntity<?> remove(@PathVariable Long recetaId) {
        String correo = getCorreoAutenticado();
        if (correo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            favoritoService.removeFavorito(correo, recetaId);
            return ResponseEntity.ok("Favorito eliminado");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    /**
     * Toggle favorito (agrega o elimina segun estado actual).
     * POST /api/favoritos/toggle/{recetaId}
     */
    @PostMapping("/toggle/{recetaId}")
    public ResponseEntity<?> toggle(@PathVariable Long recetaId) {
        String correo = getCorreoAutenticado();
        if (correo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            FavoritoToggleResponse response = favoritoService.toggleFavorito(correo, recetaId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
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

    private List<FavoritoResponse> toResponseList(List<Favorito> favoritos) {
        List<FavoritoResponse> response = new ArrayList<>();
        for (Favorito favorito : favoritos) {
            response.add(toResponse(favorito));
        }
        return response;
    }

    private FavoritoResponse toResponse(Favorito favorito) {
        FavoritoResponse response = new FavoritoResponse();
        if (favorito.getReceta() != null) {
            response.setRecetaId(favorito.getReceta().getId());
            response.setTitulo(favorito.getReceta().getTitulo());
            response.setImagenUrl(favorito.getReceta().getImagenUrl());
        }
        response.setFechaAgregado(favorito.getFechaAgregado());
        return response;
    }
}
