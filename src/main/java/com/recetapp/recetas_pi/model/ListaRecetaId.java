package com.recetapp.recetas_pi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ListaRecetaId implements Serializable {

    @Column(name = "lista_id")
    private Long listaId;

    @Column(name = "receta_id")
    private Long recetaId;

    public ListaRecetaId() {
    }

    public ListaRecetaId(Long listaId, Long recetaId) {
        this.listaId = listaId;
        this.recetaId = recetaId;
    }

    public Long getListaId() {
        return listaId;
    }

    public void setListaId(Long listaId) {
        this.listaId = listaId;
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
        ListaRecetaId that = (ListaRecetaId) o;
        return Objects.equals(listaId, that.listaId) && Objects.equals(recetaId, that.recetaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listaId, recetaId);
    }
}
