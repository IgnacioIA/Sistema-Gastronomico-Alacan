package com.Alacan.demo.Catalogo.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreProducto;
    private String descripcionReceta;
    private String descripcionAlPublico;
    private BigDecimal precioVenta;
    private boolean activo;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Receta> recetas = new ArrayList<>();

    public Producto(String nombreProducto,
                    String descripcionReceta,
                    String descripcionAlPublico,
                    BigDecimal precioVenta,
                    boolean activo) {

        if (nombreProducto == null || nombreProducto.isBlank()) {
            throw new IllegalArgumentException("Nombre requerido");
        }

        if (precioVenta == null || precioVenta.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Precio inválido");
        }

        this.nombreProducto = nombreProducto;
        this.descripcionReceta = descripcionReceta;
        this.descripcionAlPublico = descripcionAlPublico;
        this.precioVenta = precioVenta;
        this.activo = activo;
    }

    public List<Receta> getRecetas() {
        return Collections.unmodifiableList(recetas);
    }

    // 🔥 comportamiento clave
    public void agregarIngrediente(Ingrediente ingrediente, BigDecimal cantidad) {

        if (ingrediente == null) {
            throw new IllegalArgumentException("Ingrediente requerido");
        }

        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        boolean yaExiste = recetas.stream()
                .anyMatch(r -> r.esMismoIngrediente(ingrediente));

        if (yaExiste) {
            throw new IllegalArgumentException("El ingrediente ya está en la receta");
        }

        Receta receta = new Receta(this, ingrediente, cantidad);
        recetas.add(receta);
    }

    public void eliminarIngrediente(Long ingredienteId) {
        recetas.removeIf(r -> r.getIngredienteId().equals(ingredienteId));
    }

    public void modificarCantidadIngrediente(Long ingredienteId, BigDecimal nuevaCantidad) {

        if (nuevaCantidad == null || nuevaCantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        Receta receta = recetas.stream()
                .filter(r -> r.getIngredienteId().equals(ingredienteId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ingrediente no encontrado"));

        receta.modificarCantidad(nuevaCantidad);
    }

    public Long getId(){
        return id;
    }

    public String getNombreProducto(){
        return nombreProducto;
    }

    public BigDecimal getPrecioVenta(){
        return precioVenta;
    }
}
