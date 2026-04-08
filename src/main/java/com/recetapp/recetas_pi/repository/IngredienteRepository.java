package com.recetapp.recetas_pi.repository;

import com.recetapp.recetas_pi.model.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
    Optional<Ingrediente> findByNombreIgnoreCase(String nombre);
}
