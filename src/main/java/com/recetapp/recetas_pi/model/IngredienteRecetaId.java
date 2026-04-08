package com.recetapp.recetas_pi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IngredienteRecetaId implements Serializable {

    @Column(name = "receta_id")
    private Long recetaId;

    @Column(name = "ingrediente_id")
    private Long ingredienteId;

    public IngredienteRecetaId() {
    }

    public IngredienteRecetaId(Long recetaId, Long ingredienteId) {
        this.recetaId = recetaId;
        this.ingredienteId = ingredienteId;
    }

    public Long getRecetaId() {
        return recetaId;
    }

    public void setRecetaId(Long recetaId) {
        this.recetaId = recetaId;
    }

    public Long getIngredienteId() {
        return ingredienteId;
    }

    public void setIngredienteId(Long ingredienteId) {
        this.ingredienteId = ingredienteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IngredienteRecetaId that = (IngredienteRecetaId) o;
        return Objects.equals(recetaId, that.recetaId) && Objects.equals(ingredienteId, that.ingredienteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recetaId, ingredienteId);
    }
}
