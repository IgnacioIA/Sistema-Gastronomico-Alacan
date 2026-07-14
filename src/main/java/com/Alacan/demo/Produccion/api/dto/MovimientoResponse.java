package com.Alacan.demo.Produccion.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientoResponse {

    private Long id;
    private TipoMovimientoResponse tipoMovimiento;
    private String sentido;
    private BigDecimal cantidad;
    private LocalDateTime momento;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoMovimientoResponse getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimientoResponse tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public String getSentido() {
        return sentido;
    }

    public void setSentido(String sentido) {
        this.sentido = sentido;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getMomento() {
        return momento;
    }

    public void setMomento(LocalDateTime momento) {
        this.momento = momento;
    }
}
