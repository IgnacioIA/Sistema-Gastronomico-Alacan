package com.Alacan.demo.Catalogo.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"combo_id", "producto_base_id"})
    }
)
public class OpcionCombo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "combo_id")
    private Combo combo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_base_id")
    private Producto productoBase;

    @ManyToMany
    @JoinTable(
        name = "opcion_combo_reemplazos",
        joinColumns = @JoinColumn(name = "opcion_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<Producto> reemplazos = new ArrayList<>();

    public OpcionCombo(Combo combo, Producto productoBase) {

        if (combo == null) {
            throw new IllegalArgumentException("Combo requerido");
        }

        if (productoBase == null) {
            throw new IllegalArgumentException("Producto base requerido");
        }

        this.combo = combo;
        this.productoBase = productoBase;
    }

    public void agregarReemplazo(Producto producto) {

        if (producto == null) {
            throw new IllegalArgumentException("Producto inválido");
        }

        if (reemplazos.contains(producto)) {
            throw new IllegalArgumentException("Reemplazo duplicado");
        }

        reemplazos.add(producto);
    }

    public Producto getProductoBase() {
        return productoBase;
    }

    public List<Producto> getReemplazos() {
        return Collections.unmodifiableList(reemplazos);
    }
}
