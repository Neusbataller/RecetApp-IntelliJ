package com.recetapp.recetas_pi.dto.usuario;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class UsuarioAlergiasUpdateRequest {

    @NotNull
    @NotEmpty
    private List<String> alergias;

    public List<String> getAlergias() {
        return alergias;
    }
    public void setAlergias(List<String> alergias) {
        this.alergias = alergias;
    }
}
