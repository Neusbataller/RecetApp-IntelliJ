package com.recetapp.recetas_pi.repository;

import com.recetapp.recetas_pi.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    List<Favorito> findByUsuarioId(Long usuarioId);
    Optional<Favorito> findByUsuarioIdAndRecetaId(Long usuarioId, Long recetaId);
    boolean existsByUsuarioIdAndRecetaId(Long usuarioId, Long recetaId);
    
    
}