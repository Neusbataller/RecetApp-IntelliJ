package com.recetapp.recetas_pi.repository;

import com.recetapp.recetas_pi.model.ListaReceta;
import com.recetapp.recetas_pi.model.ListaRecetaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ListaRecetaRepository extends JpaRepository<ListaReceta, ListaRecetaId> {

    List<ListaReceta> findByListaIdOrderByFechaAgregadoDesc(Long listaId);

    Optional<ListaReceta> findByListaIdAndRecetaId(Long listaId, Long recetaId);

    boolean existsByListaIdAndRecetaId(Long listaId, Long recetaId);
}
