package com.recetapp.recetas_pi.controller.lista;

import com.recetapp.recetas_pi.dto.lista.ListaCreateRequest;
import com.recetapp.recetas_pi.dto.lista.ListaResponse;
import com.recetapp.recetas_pi.dto.lista.ListaUpdateRequest;
import com.recetapp.recetas_pi.model.Lista;
import com.recetapp.recetas_pi.service.ListaService;
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

@RestController
@RequestMapping("/api/listas")
@Validated
public class ListaController {

    @Autowired
    private ListaService listaService;

    @GetMapping("/mine")
    public ResponseEntity<?> getMine() {
        String correo = getCorreoAutenticado();
        if (correo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        return ResponseEntity.ok(toResponseList(listaService.getMisListas(correo)));
    }

    @GetMapping("/{listaId}")
    public ResponseEntity<?> getById(@PathVariable Long listaId) {
        String correo = getCorreoAutenticado();
        if (correo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            Lista lista = listaService.getMiListaById(correo, listaId);
            return ResponseEntity.ok(toResponse(lista));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ListaCreateRequest req) {
        String correo = getCorreoAutenticado();
        if (correo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            Lista created = listaService.crearLista(correo, req.getNombre(), req.getImagenUrl());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("Ya tienes una lista con ese nombre".equals(msg)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }

    @PutMapping("/{listaId}")
    public ResponseEntity<?> update(@PathVariable Long listaId, @Valid @RequestBody ListaUpdateRequest req) {
        String correo = getCorreoAutenticado();
        if (correo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            Lista updated = listaService.actualizarLista(correo, listaId, req.getNombre(), req.getImagenUrl());
            return ResponseEntity.ok(toResponse(updated));
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("Lista no encontrada".equals(msg)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            if ("Ya tienes una lista con ese nombre".equals(msg)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }

    @DeleteMapping("/{listaId}")
    public ResponseEntity<?> delete(@PathVariable Long listaId) {
        String correo = getCorreoAutenticado();
        if (correo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        try {
            listaService.eliminarLista(correo, listaId);
            return ResponseEntity.ok("Lista eliminada correctamente");
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

    private List<ListaResponse> toResponseList(List<Lista> listas) {
        List<ListaResponse> response = new ArrayList<>();
        for (Lista lista : listas) {
            response.add(toResponse(lista));
        }
        return response;
    }

    private ListaResponse toResponse(Lista lista) {
        ListaResponse response = new ListaResponse();
        response.setId(lista.getId());
        response.setNombre(lista.getNombre());
        response.setImagenUrl(lista.getImagenUrl());
        response.setFechaCreacion(lista.getFechaCreacion());
        int total = lista.getListaRecetas() == null ? 0 : lista.getListaRecetas().size();
        response.setTotalRecetas(total);
        return response;
    }
}
