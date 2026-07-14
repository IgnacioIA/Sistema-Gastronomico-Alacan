package com.Alacan.demo.Produccion.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// Nombre de entidad y de tabla explícitos: Hibernate usa el nombre simple de la clase como
// entity name por defecto, y colisiona con Catalogo.Receta (misma clase "Receta" en otro
// paquete) mientras ambos módulos conviven (ver ADR 0001, sección 9).
@Entity(name = "ProduccionReceta")
@Table(name = "produccion_receta")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "articulo_producido_id", nullable = false)
    private Articulo articuloProducido;

    private BigDecimal cantidadProducida;

    private Integer tiempoEstimadoMinutos;

    private boolean activo;

    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecetaDetalle> detalles = new ArrayList<>();

    public Receta(Articulo articuloProducido, BigDecimal cantidadProducida, Integer tiempoEstimadoMinutos) {

        if (articuloProducido == null) {
            throw new IllegalArgumentException("Artículo producido requerido");
        }

        if (cantidadProducida == null || cantidadProducida.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cantidad producida inválida");
        }

        if (tiempoEstimadoMinutos == null || tiempoEstimadoMinutos < 0) {
            throw new IllegalArgumentException("Tiempo estimado inválido");
        }

        this.articuloProducido = articuloProducido;
        this.cantidadProducida = cantidadProducida;
        this.tiempoEstimadoMinutos = tiempoEstimadoMinutos;
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
    }

    public void agregarDetalle(Articulo articuloRequerido, BigDecimal cantidadRequerida) {

        if (articuloRequerido == null) {
            throw new IllegalArgumentException("Artículo requerido inválido");
        }

        boolean yaExiste = detalles.stream()
                .anyMatch(d -> d.esMismoArticulo(articuloRequerido));

        if (yaExiste) {
            throw new IllegalArgumentException("El artículo ya está en la receta");
        }

        detalles.add(new RecetaDetalle(this, articuloRequerido, cantidadRequerida));
    }

    public void eliminarDetalle(Long articuloRequeridoId) {
        detalles.removeIf(d -> d.getArticuloRequeridoId().equals(articuloRequeridoId));
    }

    public void modificarCantidadDetalle(Long articuloRequeridoId, BigDecimal nuevaCantidad) {

        RecetaDetalle detalle = detalles.stream()
                .filter(d -> d.getArticuloRequeridoId().equals(articuloRequeridoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Artículo no encontrado en la receta"));

        detalle.modificarCantidad(nuevaCantidad);
    }

    public void desactivar() {
        this.activo = false;
    }

    public List<RecetaDetalle> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public Long getId() {
        return id;
    }

    public Articulo getArticuloProducido() {
        return articuloProducido;
    }

    public BigDecimal getCantidadProducida() {
        return cantidadProducida;
    }

    public Integer getTiempoEstimadoMinutos() {
        return tiempoEstimadoMinutos;
    }

    public boolean isActivo() {
        return activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}
