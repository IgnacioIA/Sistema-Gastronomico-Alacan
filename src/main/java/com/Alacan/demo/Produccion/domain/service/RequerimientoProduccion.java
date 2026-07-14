package com.Alacan.demo.Produccion.domain.service;

import java.math.BigDecimal;

import com.Alacan.demo.Produccion.domain.model.Articulo;
import com.Alacan.demo.Produccion.domain.model.FaltanteProduccion;
import com.Alacan.demo.Produccion.domain.model.Stock;

public class RequerimientoProduccion {

    private final Articulo articulo;
    private final Stock stock;
    private final BigDecimal cantidadNecesaria;

    public RequerimientoProduccion(Articulo articulo, Stock stock, BigDecimal cantidadNecesaria) {
        this.articulo = articulo;
        this.stock = stock;
        this.cantidadNecesaria = cantidadNecesaria;
    }

    public boolean esSuficiente() {
        return stock.cantidadDisponible().compareTo(cantidadNecesaria) >= 0;
    }

    public FaltanteProduccion aFaltante() {
        return new FaltanteProduccion(articulo, cantidadNecesaria, stock.cantidadDisponible());
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public Stock getStock() {
        return stock;
    }

    public BigDecimal getCantidadNecesaria() {
        return cantidadNecesaria;
    }
}
