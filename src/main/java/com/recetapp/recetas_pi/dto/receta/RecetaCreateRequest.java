package com.recetapp.recetas_pi.dto.receta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class RecetaCreateRequest {

    private Long usuarioId;

    @NotBlank
    @Size(max = 150)
    private String titulo;

    private String descripcion;

    private String preparacion;

    @Min(0)
    private Integer tiempoPreparacionMin;

    @Min(0)
    private Integer tiempoCoccionMin;

    @Min(1)
    @Max(255)
    private Integer porciones;

    @Size(max = 20)
    private String dificultad;

    @Size(max = 500)
    private String imagenUrl;

    @Valid
    private List<RecetaIngredienteRequest> ingredientes;

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

    public List<RecetaIngredienteRequest> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<RecetaIngredienteRequest> ingredientes) {
        this.ingredientes = ingredientes;
    }
}