package com.Alacan.demo.Pedido.domain.model;

import java.math.BigDecimal;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PRODUCTO")
public class ItemProducto extends ItemPedido {

    private Long productoId;
    private String nombreProducto;
    private BigDecimal precioUnitario;

    protected ItemProducto() {
    }

    public ItemProducto(Long productoId,
            String nombreProducto,
            BigDecimal precioUnitario,
            int cantidad,
            String observacion) {

        super(cantidad, observacion);

        if (productoId == null) {
            throw new IllegalArgumentException("productoId requerido");
        }

        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Precio inválido");
        }

        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
    }

    @Override
    public BigDecimal calcularSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(getCantidad()));
    }

    public Long getProductoId() {
        return productoId;
    }

    public String getNombreItemProducto() {
        return nombreProducto;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
}