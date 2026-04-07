package com.recetapp.recetas_pi.repository;

import com.recetapp.recetas_pi.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface RecetaRepository extends JpaRepository<Receta, Long> {
    
    @Query("SELECT r FROM Receta r JOIN r.categorias c WHERE c.id = :categoriaId")
    List<Receta> findByCategoriaId(Long categoriaId);

    List<Receta> findByTituloContainingIgnoreCase(String titulo);
}