package com.Alacan.demo.Catalogo.domain.model;

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
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "receta", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "producto_id", "ingrediente_id" })
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private Ingrediente ingrediente;

    @Column(nullable = false)
    private BigDecimal cantidadNecesaria;

    public Receta(Producto producto,
            Ingrediente ingrediente,
            BigDecimal cantidadNecesaria) {

        if (producto == null) {
            throw new IllegalArgumentException("Producto requerido");
        }

        if (ingrediente == null) {
            throw new IllegalArgumentException("Ingrediente requerido");
        }

        if (cantidadNecesaria == null || cantidadNecesaria.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        this.producto = producto;
        this.ingrediente = ingrediente;
        this.cantidadNecesaria = cantidadNecesaria;
    }

    public void modificarCantidad(BigDecimal nuevaCantidad) {
        if (nuevaCantidad == null || nuevaCantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }
        this.cantidadNecesaria = nuevaCantidad;
    }

    public boolean esMismoIngrediente(Ingrediente otro) {
        return this.ingrediente.getId().equals(otro.getId());
    }

    public Long getIngredienteId() {
        if (ingrediente == null) {
            throw new IllegalStateException("Ingrediente no inicializado");
        }
        return ingrediente.getId();
    }

    public BigDecimal getCantidadNecesaria() {
        return cantidadNecesaria;
    }
}
