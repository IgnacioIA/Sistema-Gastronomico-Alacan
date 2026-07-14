package com.Alacan.demo.Produccion.api.dto;

import java.math.BigDecimal;

public class CrearIngredienteRequest {

    private String nombre;
    private Long unidadMedidaId;
    private String descripcion;
    private BigDecimal stockMinimo;
    private StockInicialRequest stockInicial;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getUnidadMedidaId() {
        return unidadMedidaId;
    }

    public void setUnidadMedidaId(Long unidadMedidaId) {
        this.unidadMedidaId = unidadMedidaId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(BigDecimal stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public StockInicialRequest getStockInicial() {
        return stockInicial;
    }

    public void setStockInicial(StockInicialRequest stockInicial) {
        this.stockInicial = stockInicial;
    }
}
