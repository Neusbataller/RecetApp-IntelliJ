package com.recetapp.recetas_pi.dto.auth;

public class PasswordTokenValidationResponse {

    private boolean valido;
    private String mensaje;

    public PasswordTokenValidationResponse() {
    }

    public PasswordTokenValidationResponse(boolean valido, String mensaje) {
        this.valido = valido;
        this.mensaje = mensaje;
    }

    public boolean isValido() {
        return valido;
    }

    public void setValido(boolean valido) {
        this.valido = valido;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
