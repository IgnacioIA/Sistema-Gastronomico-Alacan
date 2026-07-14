package com.Alacan.demo.Produccion.api.dto;

import java.math.BigDecimal;

// Reutilizado por los tres casos de uso de movimiento de stock (ingreso, pérdida,
// corrección): en los tres el único dato que aporta el usuario es la cantidad. El
// TipoMovimiento y el sentido los decide cada caso de uso en el Backend.
public class RegistrarCantidadStockRequest {

    private BigDecimal cantidad;

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }
}
