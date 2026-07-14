package com.Alacan.demo.Produccion.application;

import java.math.BigDecimal;

public class DetalleRecetaInput {

    private final Long articuloRequeridoId;
    private final BigDecimal cantidadRequerida;

    public DetalleRecetaInput(Long articuloRequeridoId, BigDecimal cantidadRequerida) {
        this.articuloRequeridoId = articuloRequeridoId;
        this.cantidadRequerida = cantidadRequerida;
    }

    public Long getArticuloRequeridoId() {
        return articuloRequeridoId;
    }

    public BigDecimal getCantidadRequerida() {
        return cantidadRequerida;
    }
}
