package com.Alacan.demo.Produccion.domain.query;

public class OrdenIngrediente {

    private final CampoOrdenIngrediente campo;
    private final Direccion direccion;

    public OrdenIngrediente(CampoOrdenIngrediente campo, Direccion direccion) {

        if (campo == null) {
            throw new IllegalArgumentException("Campo de ordenamiento requerido");
        }

        if (direccion == null) {
            throw new IllegalArgumentException("Dirección de ordenamiento requerida");
        }

        this.campo = campo;
        this.direccion = direccion;
    }

    public CampoOrdenIngrediente getCampo() {
        return campo;
    }

    public Direccion getDireccion() {
        return direccion;
    }
}
