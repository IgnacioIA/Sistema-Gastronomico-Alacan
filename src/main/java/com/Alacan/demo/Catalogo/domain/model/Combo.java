package com.Alacan.demo.Catalogo.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Combo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private BigDecimal precio;

    @OneToMany(mappedBy = "combo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpcionCombo> opciones = new ArrayList<>();

    public Combo(String nombre, BigDecimal precio) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Nombre requerido");
        }

        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Precio inválido");
        }

        this.nombre = nombre;
        this.precio = precio;
    }

    public void agregarOpcion(OpcionCombo opcion) {
        opciones.add(opcion);
    }

    public List<OpcionCombo> getOpciones() {
        return Collections.unmodifiableList(opciones);
    }

    public Long getId() {
        return id;
    }

    public String getNombreCombo(){
        return nombre;
    }

    public BigDecimal getPrecioCombo(){
        return precio;
    }
}
