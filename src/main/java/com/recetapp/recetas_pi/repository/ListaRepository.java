package com.recetapp.recetas_pi.repository;

import com.recetapp.recetas_pi.model.Lista;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ListaRepository extends JpaRepository<Lista, Long> {
    List<Lista> findByUsuarioId(Long usuarioId);
}