package com.Alacan.demo.Produccion.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
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
public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_articulo_id", nullable = false)
    private TipoArticulo tipoArticulo;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_medida_id", nullable = false)
    private UnidadMedida unidadMedida;

    private String descripcion;

    private boolean activo;

    private LocalDateTime fechaCreacion;

    public Articulo(String nombre, TipoArticulo tipoArticulo, UnidadMedida unidadMedida, String descripcion) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Nombre requerido");
        }

        if (tipoArticulo == null) {
            throw new IllegalArgumentException("Tipo de artículo requerido");
        }

        if (unidadMedida == null) {
            throw new IllegalArgumentException("Unidad de medida requerida");
        }

        this.nombre = nombre;
        this.tipoArticulo = tipoArticulo;
        this.unidadMedida = unidadMedida;
        this.descripcion = descripcion;
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
    }

    public void modificarNombre(String nuevoNombre) {

        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            throw new IllegalArgumentException("Nombre requerido");
        }

        this.nombre = nuevoNombre;
    }

    public void modificarDescripcion(String nuevaDescripcion) {
        this.descripcion = nuevaDescripcion;
    }

    // La validación de si el cambio de unidad está permitido (sin stock físico ni
    // movimientos previos) es una regla que cruza agregados (Stock, Movimiento) y
    // por eso vive en el caso de uso de aplicación, no acá (ADR 0001, sección 16).
    public void modificarUnidadMedida(UnidadMedida nuevaUnidadMedida) {

        if (nuevaUnidadMedida == null) {
            throw new IllegalArgumentException("Unidad de medida requerida");
        }

        this.unidadMedida = nuevaUnidadMedida;
    }

    // Nunca elimina: preserva recetas, movimientos y stock existentes (ADR 0001, sección 15).
    // Idempotente a propósito — desactivar un artículo ya inactivo no es un error.
    public void desactivar() {
        this.activo = false;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoArticulo getTipoArticulo() {
        return tipoArticulo;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}
