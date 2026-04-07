package com.recetapp.recetas_pi.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recetas")
@Data
public class Receta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(columnDefinition = "TEXT")
    private String preparacion;

    @Column(name = "tiempo_minutos")
    private Integer tiempoMinutos;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL)
    private List<Ingrediente> ingredientes;

    @ManyToMany
    @JoinTable(
        name = "receta_categorias",
        joinColumns = @JoinColumn(name = "receta_id"),
        inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<Categoria> categorias;

    // Explicit getter for id (useful if Lombok is not active in the IDE)
    public Long getId() {
        return id;
    }
}