package com.Alacan.demo.Produccion.api.dto;

public class IngredienteResponse {

    private Long id;
    private String nombre;
    private TipoArticuloResponse tipoArticulo;
    private UnidadMedidaResponse unidadMedida;
    private String descripcion;
    private boolean activo;
    private StockResponse stock;

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

    public TipoArticuloResponse getTipoArticulo() {
        return tipoArticulo;
    }

    public void setTipoArticulo(TipoArticuloResponse tipoArticulo) {
        this.tipoArticulo = tipoArticulo;
    }

    public UnidadMedidaResponse getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedidaResponse unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public StockResponse getStock() {
        return stock;
    }

    public void setStock(StockResponse stock) {
        this.stock = stock;
    }
}
