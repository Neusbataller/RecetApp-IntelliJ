package com.recetapp.recetas_pi.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "alergias")
@Data
public class Alergia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nombre;
}
