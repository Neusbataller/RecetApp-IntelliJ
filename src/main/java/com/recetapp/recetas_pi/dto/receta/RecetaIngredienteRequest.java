package com.recetapp.recetas_pi.dto.receta;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class RecetaIngredienteRequest {

    @NotBlank
    @Size(max = 150)
    private String nombre;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal cantidad;

    @Size(max = 50)
    private String unidad;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
