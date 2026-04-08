package com.recetapp.recetas_pi.dto.valoracion;

public class ValoracionStatsResponse {
    private Long recetaId;
    private Double mediaPuntuacion;
    private Long totalValoraciones;

    public Long getRecetaId() {
        return recetaId;
    }

    public void setRecetaId(Long recetaId) {
        this.recetaId = recetaId;
    }

    public Double getMediaPuntuacion() {
        return mediaPuntuacion;
    }

    public void setMediaPuntuacion(Double mediaPuntuacion) {
        this.mediaPuntuacion = mediaPuntuacion;
    }

    public Long getTotalValoraciones() {
        return totalValoraciones;
    }

    public void setTotalValoraciones(Long totalValoraciones) {
        this.totalValoraciones = totalValoraciones;
    }
}
