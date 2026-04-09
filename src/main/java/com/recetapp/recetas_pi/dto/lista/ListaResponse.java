package com.recetapp.recetas_pi.dto.lista;

import java.time.LocalDateTime;

public class ListaResponse {
    private Long id;
    private String nombre;
    private String imagenUrl;
    private LocalDateTime fechaCreacion;
    private Integer totalRecetas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getTotalRecetas() {
        return totalRecetas;
    }

    public void setTotalRecetas(Integer totalRecetas) {
        this.totalRecetas = totalRecetas;
    }
}
