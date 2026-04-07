package com.recetapp.recetas_pi.dto.usuario;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class UsuarioAlergiasUpdateRequest {

    @NotNull
    private List<String> alergias;

    public List<String> getAlergias() {
        return alergias;
    }
    //pepe
    public void setAlergias(List<String> alergias) {
        this.alergias = alergias;
    }
}
