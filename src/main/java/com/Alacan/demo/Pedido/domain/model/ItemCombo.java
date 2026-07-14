package com.Alacan.demo.Pedido.domain.model;

import java.math.BigDecimal;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("COMBO")
public class ItemCombo extends ItemPedido {

    private Long comboId;
    private String nombreCombo;
    private BigDecimal precioCombo;

    protected ItemCombo() {
    }

    public ItemCombo(Long comboId,
            String nombreCombo,
            BigDecimal precioCombo,
            int cantidad,
            String observacion) {

        super(cantidad, observacion);

        if (comboId == null) {
            throw new IllegalArgumentException("comboId requerido");
        }

        if (precioCombo == null || precioCombo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Precio inválido");
        }

        this.comboId = comboId;
        this.nombreCombo = nombreCombo;
        this.precioCombo = precioCombo;
    }

    @Override
    public BigDecimal calcularSubtotal() {
        return precioCombo.multiply(BigDecimal.valueOf(getCantidad()));
    }

    public Long getComboId() {
        return comboId;
    }

    public String getNombreCombo() {
        return nombreCombo;
    }

    public BigDecimal getPrecioCombo() {
        return precioCombo;
    }
}
