package com.recetapp.recetas_pi.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "ingredientes")
@Data
public class Ingrediente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(length = 50)
    private String unidad;
}