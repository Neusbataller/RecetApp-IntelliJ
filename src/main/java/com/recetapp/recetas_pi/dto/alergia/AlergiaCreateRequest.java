package com.recetapp.recetas_pi.dto.alergia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AlergiaCreateRequest {

    @NotBlank
    @Size(max = 150)
    private String nombre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
