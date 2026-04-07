package com.recetapp.recetas_pi.repository;

import com.recetapp.recetas_pi.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}