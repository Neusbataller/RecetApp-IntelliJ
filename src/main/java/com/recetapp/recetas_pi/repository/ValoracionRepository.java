package com.recetapp.recetas_pi.repository;

import com.recetapp.recetas_pi.model.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    List<Valoracion> findByRecetaIdOrderByFechaCreacionDesc(Long recetaId);

    Optional<Valoracion> findByUsuarioCorreoAndRecetaId(String correo, Long recetaId);

    boolean existsByUsuarioCorreoAndRecetaId(String correo, Long recetaId);

    long countByRecetaId(Long recetaId);

    @Query("select coalesce(avg(v.puntuacion), 0) from Valoracion v where v.receta.id = :recetaId")
    Double averageByRecetaId(@Param("recetaId") Long recetaId);
}