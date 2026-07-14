package com.Alacan.demo.Produccion.application;

import java.util.Collections;
import java.util.List;

import com.Alacan.demo.Produccion.domain.model.FaltanteProduccion;

public class ResultadoDisponibilidad {

    private final boolean disponible;
    private final List<FaltanteProduccion> faltantes;

    public ResultadoDisponibilidad(boolean disponible, List<FaltanteProduccion> faltantes) {
        this.disponible = disponible;
        this.faltantes = faltantes;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public List<FaltanteProduccion> getFaltantes() {
        return Collections.unmodifiableList(faltantes);
    }
}
