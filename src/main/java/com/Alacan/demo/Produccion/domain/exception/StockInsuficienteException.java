package com.Alacan.demo.Produccion.domain.exception;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.Alacan.demo.Produccion.domain.model.FaltanteProduccion;

public class StockInsuficienteException extends ProduccionException {

    private final List<FaltanteProduccion> faltantes;

    public StockInsuficienteException(List<FaltanteProduccion> faltantes) {
        super(construirMensaje(faltantes));
        this.faltantes = faltantes;
    }

    private static String construirMensaje(List<FaltanteProduccion> faltantes) {
        String detalle = faltantes.stream()
                .map(f -> f.getArticulo().getNombre() + " (faltan " + f.getCantidadFaltante() + ")")
                .collect(Collectors.joining(", "));
        return "Stock insuficiente para producir. Artículos faltantes: " + detalle;
    }

    public List<FaltanteProduccion> getFaltantes() {
        return Collections.unmodifiableList(faltantes);
    }
}
