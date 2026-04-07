package com.recetapp.recetas_pi.dto.usuario;

import java.time.LocalDateTime;

public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String apellidos;
    private String correo;
    private java.util.List<String> alergias;
    private LocalDateTime fechaCreacion;

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

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public java.util.List<String> getAlergias() {
        return alergias;
    }

    public void setAlergias(java.util.List<String> alergias) {
        this.alergias = alergias;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
