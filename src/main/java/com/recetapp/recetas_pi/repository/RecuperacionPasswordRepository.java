package com.recetapp.recetas_pi.repository;

import com.recetapp.recetas_pi.model.RecuperacionPassword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecuperacionPasswordRepository extends JpaRepository<RecuperacionPassword, Long> {

    Optional<RecuperacionPassword> findByToken(String token);

    void deleteByUsuarioIdAndUsadoFalse(Long usuarioId);
}
