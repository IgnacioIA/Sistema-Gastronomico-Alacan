package com.Alacan.demo.Produccion.domain.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "produccion_receta_detalle", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "receta_id", "articulo_requerido_id" })
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecetaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "articulo_requerido_id", nullable = false)
    private Articulo articuloRequerido;

    @Column(nullable = false)
    private BigDecimal cantidadRequerida;

    public RecetaDetalle(Receta receta, Articulo articuloRequerido, BigDecimal cantidadRequerida) {

        if (receta == null) {
            throw new IllegalArgumentException("Receta requerida");
        }

        if (articuloRequerido == null) {
            throw new IllegalArgumentException("Artículo requerido inválido");
        }

        if (cantidadRequerida == null || cantidadRequerida.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        this.receta = receta;
        this.articuloRequerido = articuloRequerido;
        this.cantidadRequerida = cantidadRequerida;
    }

    public void modificarCantidad(BigDecimal nuevaCantidad) {
        if (nuevaCantidad == null || nuevaCantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }
        this.cantidadRequerida = nuevaCantidad;
    }

    public boolean esMismoArticulo(Articulo otro) {
        return this.articuloRequerido.getId().equals(otro.getId());
    }

    public Articulo getArticuloRequerido() {
        return articuloRequerido;
    }

    public Long getArticuloRequeridoId() {
        if (articuloRequerido == null) {
            throw new IllegalStateException("Artículo requerido no inicializado");
        }
        return articuloRequerido.getId();
    }

    public BigDecimal getCantidadRequerida() {
        return cantidadRequerida;
    }
}
