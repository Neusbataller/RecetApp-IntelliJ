package com.recetapp.recetas_pi.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ingredientes")
@Data
public class Ingrediente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nombre;

    // Explicit getters and setters (useful if Lombok is not active in the IDE)
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}