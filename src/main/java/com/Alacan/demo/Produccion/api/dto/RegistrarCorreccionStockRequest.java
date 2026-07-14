package com.Alacan.demo.Produccion.api.dto;

import java.math.BigDecimal;

// A diferencia de ingreso/perdida, el usuario informa la cantidad real contada,
// no cuanto quiere sumar o restar - esa direccion la calcula el Backend.
public class RegistrarCorreccionStockRequest {

    private BigDecimal cantidadReal;

    public BigDecimal getCantidadReal() {
        return cantidadReal;
    }

    public void setCantidadReal(BigDecimal cantidadReal) {
        this.cantidadReal = cantidadReal;
    }
}
