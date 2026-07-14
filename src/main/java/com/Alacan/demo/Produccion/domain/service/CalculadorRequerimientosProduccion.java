package com.Alacan.demo.Produccion.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.Alacan.demo.Produccion.domain.model.Articulo;
import com.Alacan.demo.Produccion.domain.model.Receta;
import com.Alacan.demo.Produccion.domain.model.RecetaDetalle;
import com.Alacan.demo.Produccion.domain.model.Stock;
import com.Alacan.demo.Produccion.domain.repository.StockRepository;

// La receta define una produccion base; toda cantidad se escala proporcionalmente
// respecto de esa base para calcular cuanto se necesita realmente de cada articulo.
public class CalculadorRequerimientosProduccion {

    private static final int ESCALA = 6;

    private final StockRepository stockRepository;

    public CalculadorRequerimientosProduccion(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<RequerimientoProduccion> calcular(Receta receta, BigDecimal cantidadAProducir) {

        List<RequerimientoProduccion> requerimientos = new ArrayList<>();

        for (RecetaDetalle detalle : receta.getDetalles()) {

            Articulo articuloRequerido = detalle.getArticuloRequerido();

            BigDecimal cantidadNecesaria = detalle.getCantidadRequerida()
                    .multiply(cantidadAProducir)
                    .divide(receta.getCantidadProducida(), ESCALA, RoundingMode.CEILING);

            Stock stock = stockRepository.buscarPorArticuloId(articuloRequerido.getId())
                    .orElseThrow(() -> new IllegalStateException(
                            "El artículo " + articuloRequerido.getId() + " no tiene stock inicializado"));

            requerimientos.add(new RequerimientoProduccion(articuloRequerido, stock, cantidadNecesaria));
        }

        return requerimientos;
    }
}
