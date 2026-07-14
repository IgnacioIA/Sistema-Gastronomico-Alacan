package com.Alacan.demo.Produccion.application;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.Alacan.demo.Produccion.domain.exception.RecetaNoEncontradaException;
import com.Alacan.demo.Produccion.domain.model.FaltanteProduccion;
import com.Alacan.demo.Produccion.domain.model.Receta;
import com.Alacan.demo.Produccion.domain.repository.RecetaRepository;
import com.Alacan.demo.Produccion.domain.repository.StockRepository;
import com.Alacan.demo.Produccion.domain.service.CalculadorRequerimientosProduccion;
import com.Alacan.demo.Produccion.domain.service.RequerimientoProduccion;

@Service
public class ConsultarDisponibilidadService {

    private final RecetaRepository recetaRepository;
    private final StockRepository stockRepository;

    public ConsultarDisponibilidadService(RecetaRepository recetaRepository, StockRepository stockRepository) {
        this.recetaRepository = recetaRepository;
        this.stockRepository = stockRepository;
    }

    public ResultadoDisponibilidad ejecutar(Long recetaId, BigDecimal cantidadAProducir) {

        Receta receta = recetaRepository.buscarPorId(recetaId)
                .orElseThrow(() -> new RecetaNoEncontradaException(recetaId));

        CalculadorRequerimientosProduccion calculador = new CalculadorRequerimientosProduccion(stockRepository);

        List<RequerimientoProduccion> requerimientos = calculador.calcular(receta, cantidadAProducir);

        List<FaltanteProduccion> faltantes = requerimientos.stream()
                .filter(r -> !r.esSuficiente())
                .map(RequerimientoProduccion::aFaltante)
                .toList();

        return new ResultadoDisponibilidad(faltantes.isEmpty(), faltantes);
    }
}
