package com.recetapp.recetas_pi.repository;

import com.recetapp.recetas_pi.model.Favorito;
import com.recetapp.recetas_pi.model.FavoritoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoritoRepository extends JpaRepository<Favorito, FavoritoId> {

    List<Favorito> findByUsuarioCorreoOrderByFechaAgregadoDesc(String correo);

    Optional<Favorito> findByUsuarioCorreoAndRecetaId(String correo, Long recetaId);

    boolean existsByUsuarioCorreoAndRecetaId(String correo, Long recetaId);

    long countByRecetaId(Long recetaId);
}