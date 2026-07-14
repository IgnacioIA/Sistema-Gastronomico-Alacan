package com.Alacan.demo.Produccion.domain.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = { "articulo_id" })
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "articulo_id", nullable = false, unique = true)
    private Articulo articulo;

    private BigDecimal cantidadActual;

    private BigDecimal cantidadReservada;

    private BigDecimal stockMinimo;

    public Stock(Articulo articulo) {
        this(articulo, BigDecimal.ZERO);
    }

    public Stock(Articulo articulo, BigDecimal stockMinimo) {

        if (articulo == null) {
            throw new IllegalArgumentException("Artículo requerido");
        }

        if (stockMinimo == null || stockMinimo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Stock mínimo inválido");
        }

        this.articulo = articulo;
        this.cantidadActual = BigDecimal.ZERO;
        this.cantidadReservada = BigDecimal.ZERO;
        this.stockMinimo = stockMinimo;
    }

    public BigDecimal cantidadDisponible() {
        return cantidadActual.subtract(cantidadReservada);
    }

    public boolean necesitaReposicion() {
        return cantidadActual.compareTo(stockMinimo) < 0;
    }

    public void registrarConsumo(BigDecimal cantidad) {

        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        if (cantidadDisponible().compareTo(cantidad) < 0) {
            throw new IllegalStateException("Stock insuficiente para registrar el consumo");
        }

        this.cantidadActual = this.cantidadActual.subtract(cantidad);
    }

    public void registrarIngreso(BigDecimal cantidad) {

        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        this.cantidadActual = this.cantidadActual.add(cantidad);
    }

    public void modificarStockMinimo(BigDecimal nuevoStockMinimo) {

        if (nuevoStockMinimo == null || nuevoStockMinimo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Stock mínimo inválido");
        }

        this.stockMinimo = nuevoStockMinimo;
    }

    public Long getId() {
        return id;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public BigDecimal getCantidadActual() {
        return cantidadActual;
    }

    public BigDecimal getCantidadReservada() {
        return cantidadReservada;
    }

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }
}
