package com.Alacan.demo.Produccion.api.dto;

import java.math.BigDecimal;

public class StockResponse {

    private BigDecimal cantidadActual;
    private BigDecimal cantidadReservada;
    private BigDecimal cantidadDisponible;
    private BigDecimal stockMinimo;
    private boolean necesitaReposicion;

    public BigDecimal getCantidadActual() {
        return cantidadActual;
    }

    public void setCantidadActual(BigDecimal cantidadActual) {
        this.cantidadActual = cantidadActual;
    }

    public BigDecimal getCantidadReservada() {
        return cantidadReservada;
    }

    public void setCantidadReservada(BigDecimal cantidadReservada) {
        this.cantidadReservada = cantidadReservada;
    }

    public BigDecimal getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(BigDecimal cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(BigDecimal stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public boolean isNecesitaReposicion() {
        return necesitaReposicion;
    }

    public void setNecesitaReposicion(boolean necesitaReposicion) {
        this.necesitaReposicion = necesitaReposicion;
    }
}
