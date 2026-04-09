package com.recetapp.recetas_pi.repository;

import com.recetapp.recetas_pi.model.Lista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ListaRepository extends JpaRepository<Lista, Long> {
    List<Lista> findByUsuarioId(Long usuarioId);

    List<Lista> findByUsuarioCorreoOrderByFechaCreacionDesc(String correo);

    Optional<Lista> findByIdAndUsuarioCorreo(Long id, String correo);

    boolean existsByUsuarioCorreoAndNombreIgnoreCase(String correo, String nombre);
}