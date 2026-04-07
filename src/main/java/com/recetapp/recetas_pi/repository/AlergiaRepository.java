package com.recetapp.recetas_pi.repository;

import com.recetapp.recetas_pi.model.Alergia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AlergiaRepository extends JpaRepository<Alergia, Long> {
    Optional<Alergia> findByNombre(String nombre);

    List<Alergia> findByNombreIn(List<String> nombres);
}
