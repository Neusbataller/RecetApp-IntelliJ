package com.recetapp.recetas_pi.repository;

import com.recetapp.recetas_pi.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecetaRepository extends JpaRepository<Receta, Long> {

    @Query("SELECT r FROM Receta r JOIN r.categorias c WHERE c.id = :categoriaId")
    List<Receta> findByCategoriaId(@Param("categoriaId") Long categoriaId);

    List<Receta> findByTituloContainingIgnoreCase(String titulo);

    List<Receta> findByUsuarioCorreoOrderByFechaCreacionDesc(String correo);

    List<Receta> findByDificultadIgnoreCase(String dificultad);

    @Query("""
            SELECT r
            FROM Receta r
            WHERE COALESCE(r.tiempoPreparacionMin, 0) + COALESCE(r.tiempoCoccionMin, 0) <= :maxTiempo
            """)
    List<Receta> findByTiempoTotalMax(@Param("maxTiempo") int maxTiempo);

    @Query("SELECT DISTINCT r FROM Receta r JOIN r.ingredientesReceta ir JOIN ir.ingrediente i WHERE LOWER(i.nombre) = :ingrediente")
    List<Receta> findByIngredienteNombre(@Param("ingrediente") String ingrediente);

    @Query("SELECT DISTINCT r FROM Receta r JOIN r.ingredientesReceta ir JOIN ir.ingrediente i WHERE LOWER(i.nombre) IN :ingredientes")
    List<Receta> findByAnyIngredienteNombres(@Param("ingredientes") List<String> ingredientes);

    @Query("""
            SELECT r
            FROM Receta r
            JOIN r.ingredientesReceta ir
            JOIN ir.ingrediente i
            WHERE LOWER(i.nombre) IN :ingredientes
            GROUP BY r.id
            HAVING COUNT(DISTINCT LOWER(i.nombre)) = :total
            """)
    List<Receta> findByAllIngredienteNombres(
            @Param("ingredientes") List<String> ingredientes,
            @Param("total") long total
    );

    @Query("SELECT DISTINCT r FROM Receta r JOIN r.categorias c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :tag, '%'))")
    List<Receta> findByTag(@Param("tag") String tag);
}