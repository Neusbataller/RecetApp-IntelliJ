package com.recetapp.recetas_pi.service;

import com.recetapp.recetas_pi.model.Alergia;
import com.recetapp.recetas_pi.repository.AlergiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlergiaService {

    @Autowired
    private AlergiaRepository alergiaRepository;

    public List<Alergia> getAll() {
        return alergiaRepository.findAll();
    }

    public Optional<Alergia> getById(Long id) {
        return alergiaRepository.findById(id);
    }

    public Alergia create(String nombre) {
        String limpio = nombre.trim();
        if (alergiaRepository.findByNombre(limpio).isPresent()) {
            throw new RuntimeException("La alergia ya existe");
        }

        Alergia alergia = new Alergia();
        alergia.setNombre(limpio);
        return alergiaRepository.save(alergia);
    }

    public Alergia update(Long id, String nombre) {
        Alergia alergia = alergiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alergia no encontrada"));

        String limpio = nombre.trim();
        Optional<Alergia> existente = alergiaRepository.findByNombre(limpio);
        if (existente.isPresent() && !existente.get().getId().equals(id)) {
            throw new RuntimeException("Ya existe una alergia con ese nombre");
        }

        alergia.setNombre(limpio);
        return alergiaRepository.save(alergia);
    }

    public void deleteById(Long id) {
        if (!alergiaRepository.existsById(id)) {
            throw new RuntimeException("Alergia no encontrada");
        }
        alergiaRepository.deleteById(id);
    }
}
