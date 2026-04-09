
package com.recetapp.recetas_pi.controller.lista;

import com.recetapp.recetas_pi.dto.lista.ListaRecetaResponse;
import com.recetapp.recetas_pi.model.ListaReceta;
import com.recetapp.recetas_pi.service.ListaService;
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
@RequestMapping("/api/listas/{listaId}/recetas")
@Validated
public class ListaRecetaController {

    @Autowired
    private ListaService listaService;

    @GetMapping
    public ResponseEntity<?> getByLista(@PathVariable Long listaId) {
        String correo = getCorreoAutenticado();
        if (correo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            List<ListaReceta> recetas = listaService.getRecetasDeLista(correo, listaId);
            return ResponseEntity.ok(toResponseList(recetas));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/{recetaId}")
    public ResponseEntity<?> addReceta(@PathVariable Long listaId, @PathVariable Long recetaId) {
        String correo = getCorreoAutenticado();
        if (correo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            ListaReceta created = listaService.addRecetaALista(correo, listaId, recetaId);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("Lista no encontrada".equals(msg) || "Receta no encontrada".equals(msg)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            if ("La receta ya esta en la lista".equals(msg)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }

    @DeleteMapping("/{recetaId}")
    public ResponseEntity<?> removeReceta(@PathVariable Long listaId, @PathVariable Long recetaId) {
        String correo = getCorreoAutenticado();
        if (correo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            listaService.removeRecetaDeLista(correo, listaId, recetaId);
            return ResponseEntity.ok("Receta eliminada de la lista");
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("Lista no encontrada".equals(msg) || "La receta no esta en la lista".equals(msg)) {
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

    private List<ListaRecetaResponse> toResponseList(List<ListaReceta> listaRecetas) {
        List<ListaRecetaResponse> response = new ArrayList<>();
        for (ListaReceta listaReceta : listaRecetas) {
            response.add(toResponse(listaReceta));
        }
        return response;
    }

    private ListaRecetaResponse toResponse(ListaReceta listaReceta) {
        ListaRecetaResponse response = new ListaRecetaResponse();
        if (listaReceta.getReceta() != null) {
            response.setRecetaId(listaReceta.getReceta().getId());
            response.setTitulo(listaReceta.getReceta().getTitulo());
            response.setImagenUrl(listaReceta.getReceta().getImagenUrl());
        }
        response.setFechaAgregado(listaReceta.getFechaAgregado());
        return response;
    }
}
