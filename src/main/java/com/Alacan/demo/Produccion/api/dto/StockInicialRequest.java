package com.Alacan.demo.Produccion.api.dto;

import java.math.BigDecimal;

public class StockInicialRequest {

    private BigDecimal cantidad;

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }
}
