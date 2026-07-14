package com.Alacan.demo.Produccion.domain.model;

import java.math.BigDecimal;

public class FaltanteProduccion {

    private final Articulo articulo;
    private final BigDecimal cantidadRequerida;
    private final BigDecimal cantidadDisponible;

    public FaltanteProduccion(Articulo articulo, BigDecimal cantidadRequerida, BigDecimal cantidadDisponible) {
        this.articulo = articulo;
        this.cantidadRequerida = cantidadRequerida;
        this.cantidadDisponible = cantidadDisponible;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public BigDecimal getCantidadRequerida() {
        return cantidadRequerida;
    }

    public BigDecimal getCantidadDisponible() {
        return cantidadDisponible;
    }

    public BigDecimal getCantidadFaltante() {
        return cantidadRequerida.subtract(cantidadDisponible);
    }
}
