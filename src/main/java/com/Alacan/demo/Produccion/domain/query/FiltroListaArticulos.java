package com.Alacan.demo.Produccion.domain.query;

public class FiltroListaArticulos {

    private final String search;
    private final Boolean activo;
    private final Long unidadMedidaId;
    private final FiltroInventario filtroInventario;

    public FiltroListaArticulos(String search, Boolean activo, Long unidadMedidaId, FiltroInventario filtroInventario) {
        this.search = search;
        this.activo = activo;
        this.unidadMedidaId = unidadMedidaId;
        this.filtroInventario = filtroInventario;
    }

    public String getSearch() {
        return search;
    }

    public Boolean getActivo() {
        return activo;
    }

    public Long getUnidadMedidaId() {
        return unidadMedidaId;
    }

    public FiltroInventario getFiltroInventario() {
        return filtroInventario;
    }
}
