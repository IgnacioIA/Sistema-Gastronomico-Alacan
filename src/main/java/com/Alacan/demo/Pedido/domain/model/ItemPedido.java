package com.Alacan.demo.Pedido.domain.model;

import java.math.BigDecimal;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.Table;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_item")
@Table(name = "items_pedido")
public abstract class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected int cantidad;
    protected String observacion;

    protected ItemPedido() {}

    protected ItemPedido(int cantidad, String observacion) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }
        this.cantidad = cantidad;
        this.observacion = observacion;
    }

    public void aumentarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }
        this.cantidad += cantidad;
    }

    public void disminuirCantidad(int cantidad) {
        if (cantidad <= 0 || this.cantidad - cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
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

    public abstract BigDecimal calcularSubtotal();

    public Long getId() {
        return id;
    }

    public int getCantidad() {
        return cantidad;
    }
}
