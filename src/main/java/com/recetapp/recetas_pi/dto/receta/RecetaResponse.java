package com.recetapp.recetas_pi.dto.receta;

import java.time.LocalDateTime;
import java.util.List;

public class RecetaResponse {
    private Long id;
    private Long usuarioId;
    private String titulo;
    private String descripcion;
    private String preparacion;
    private Integer tiempoPreparacionMin;
    private Integer tiempoCoccionMin;
    private Integer porciones;
    private String dificultad;
    private String imagenUrl;
    private LocalDateTime fechaCreacion;
    private List<RecetaIngredienteResponse> ingredientes;
    private List<String> tags;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPreparacion() {
        return preparacion;
    }

    public void setPreparacion(String preparacion) {
        this.preparacion = preparacion;
    }

    public Integer getTiempoPreparacionMin() {
        return tiempoPreparacionMin;
    }

    public void setTiempoPreparacionMin(Integer tiempoPreparacionMin) {
        this.tiempoPreparacionMin = tiempoPreparacionMin;
    }

    public Integer getTiempoCoccionMin() {
        return tiempoCoccionMin;
    }

    public void setTiempoCoccionMin(Integer tiempoCoccionMin) {
        this.tiempoCoccionMin = tiempoCoccionMin;
    }

    public Integer getPorciones() {
        return porciones;
    }

    public void setPorciones(Integer porciones) {
        this.porciones = porciones;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
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

    public List<RecetaIngredienteResponse> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<RecetaIngredienteResponse> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}