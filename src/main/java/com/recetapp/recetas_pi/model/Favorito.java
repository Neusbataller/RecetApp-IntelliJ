package com.recetapp.recetas_pi.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "favoritos")
@Data
public class Favorito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;

    @Column(name = "fecha_agregado")
    private LocalDateTime fechaAgregado;

    // Explicit setters (useful if Lombok is not active in the IDE)
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setReceta(Receta receta) {
        this.receta = receta;
    }

    public void setFechaAgregado(LocalDateTime fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }
}