package com.recetapp.recetas_pi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FavoritoId implements Serializable {

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "receta_id")
    private Long recetaId;

    public FavoritoId() {
    }

    public FavoritoId(Long usuarioId, Long recetaId) {
        this.usuarioId = usuarioId;
        this.recetaId = recetaId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getRecetaId() {
        return recetaId;
    }

    public void setRecetaId(Long recetaId) {
        this.recetaId = recetaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoritoId that = (FavoritoId) o;
        return Objects.equals(usuarioId, that.usuarioId) && Objects.equals(recetaId, that.recetaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, recetaId);
    }
}
