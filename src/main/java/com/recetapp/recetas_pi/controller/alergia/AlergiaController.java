package com.recetapp.recetas_pi.controller.alergia;

import com.recetapp.recetas_pi.dto.alergia.AlergiaCreateRequest;
import com.recetapp.recetas_pi.dto.alergia.AlergiaResponse;
import com.recetapp.recetas_pi.model.Alergia;
import com.recetapp.recetas_pi.service.AlergiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/alergias")
@Validated
public class AlergiaController {

    @Autowired
    private AlergiaService alergiaService;

    @GetMapping
    public ResponseEntity<List<AlergiaResponse>> getAll() {
        List<Alergia> alergias = alergiaService.getAll();
        List<AlergiaResponse> response = new ArrayList<>();
        for (Alergia a : alergias) {
            response.add(toResponse(a));
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Alergia> alergia = alergiaService.getById(id);
        if (alergia.isPresent()) {
            return ResponseEntity.ok(toResponse(alergia.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alergia no encontrada");
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody AlergiaCreateRequest req) {
        try {
            Alergia created = alergiaService.create(req.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            alergiaService.deleteById(id);
            return ResponseEntity.ok("Alergia eliminada correctamente");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    private AlergiaResponse toResponse(Alergia a) {
        AlergiaResponse response = new AlergiaResponse();
        response.setId(a.getId());
        response.setNombre(a.getNombre());
        return response;
    }
}
