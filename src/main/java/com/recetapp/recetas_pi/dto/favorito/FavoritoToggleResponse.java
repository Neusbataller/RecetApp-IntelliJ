package com.recetapp.recetas_pi.dto.favorito;

public class FavoritoToggleResponse {
    private boolean favorito;
    private String mensaje;

    public boolean isFavorito() {
        return favorito;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
