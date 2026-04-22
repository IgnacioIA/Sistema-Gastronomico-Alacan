package com.Alacan.demo.Pedido.domain.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "items_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private BigDecimal precioUnitario;
    private int cantidad;
    private String observacion;

    public ItemPedido(Long productoId, String nombreProducto, BigDecimal precioUnitario, int cantidad,
            String observacion) {

        if (productoId == null) {
            throw new IllegalArgumentException("El productoId no puede ser null");
        }

        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.observacion = observacion;
    }

    public BigDecimal calcularSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public void aumentarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }
        this.cantidad += cantidad;
    }

    public void disminuirCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        if (this.cantidad - cantidad < 0) {
            throw new IllegalArgumentException("No se puede dejar en negativo");
        }

        this.cantidad -= cantidad;
    }

    public void modificarCantidad(int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        this.cantidad = nuevaCantidad;
    }

    public void cambiarObservacion(String observacion) {
        this.observacion = observacion;
    }

    public boolean esDelProducto(Long productoId) {
        return this.productoId.equals(productoId);
    }

    public boolean quedariaEnCero(int cantidad) {
        return this.cantidad == cantidad;
    }

    public Long getId() {
        return id;
    }

    public Long getProductoId(){
        return productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public String getNombreProducto() {
        return nombreProducto;
    }
}
