package com.Alacan.demo.Produccion.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "articulo_id", nullable = false)
    private Articulo articulo;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_movimiento_id", nullable = false)
    private TipoMovimiento tipoMovimiento;

    @Enumerated(EnumType.STRING)
    private Sentido sentido;

    private BigDecimal cantidad;

    private LocalDateTime momento;

    public Movimiento(Articulo articulo, TipoMovimiento tipoMovimiento, Sentido sentido, BigDecimal cantidad) {

        if (articulo == null) {
            throw new IllegalArgumentException("Artículo requerido");
        }

        if (tipoMovimiento == null) {
            throw new IllegalArgumentException("Tipo de movimiento requerido");
        }

        if (sentido == null) {
            throw new IllegalArgumentException("Sentido requerido");
        }

        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        this.articulo = articulo;
        this.tipoMovimiento = tipoMovimiento;
        this.sentido = sentido;
        this.cantidad = cantidad;
        this.momento = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public Sentido getSentido() {
        return sentido;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public LocalDateTime getMomento() {
        return momento;
    }
}
