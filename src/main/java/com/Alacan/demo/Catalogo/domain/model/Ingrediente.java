package com.Alacan.demo.Catalogo.domain.model;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreIngrediente;

    @Enumerated(EnumType.STRING)
    private UnidadMedida unidad;

    public Ingrediente(String nombreIngrediente, UnidadMedida unidad) {

        if (nombreIngrediente == null || nombreIngrediente.isBlank()) {
            throw new IllegalArgumentException("Nombre requerido");
        }

        if (unidad == null) {
            throw new IllegalArgumentException("Unidad requerida");
        }

        this.nombreIngrediente = nombreIngrediente;
        this.unidad = unidad;
    }

    public Long getId() {
        return id;
    }
}
