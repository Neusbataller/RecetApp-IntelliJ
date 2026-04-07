package com.recetapp.recetas_pi.service;

import com.recetapp.recetas_pi.model.Receta;
import com.recetapp.recetas_pi.repository.RecetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RecetaService {

    @Autowired
    private RecetaRepository recetaRepository;

    public List<Receta> getAllRecetas() {
        return recetaRepository.findAll();
    }

    public Optional<Receta> getRecetaById(Long id) {
        return recetaRepository.findById(id);
    }

    public List<Receta> getRecetasByCategoria(Long categoriaId) {
        return recetaRepository.findByCategoriaId(categoriaId);
    }

    public List<Receta> buscarRecetas(String titulo) {
        return recetaRepository.findByTituloContainingIgnoreCase(titulo);
    }
}