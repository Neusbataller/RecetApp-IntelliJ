package com.recetapp.recetas_pi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "ingredientes_receta")
@Data
public class IngredienteReceta {

    @EmbeddedId
    private IngredienteRecetaId id = new IngredienteRecetaId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("recetaId")
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ingredienteId")
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private Ingrediente ingrediente;

    @Column(precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(length = 50)
    private String unidad;

    public IngredienteRecetaId getId() {
        return id;
    }

    public void setId(IngredienteRecetaId id) {
        this.id = id;
    }

    public Receta getReceta() {
        return receta;
    }

    public void setReceta(Receta receta) {
        this.receta = receta;
    }

    public Ingrediente getIngrediente() {
        return ingrediente;
    }

    public void setIngrediente(Ingrediente ingrediente) {
        this.ingrediente = ingrediente;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }
}
