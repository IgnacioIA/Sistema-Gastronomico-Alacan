package com.Alacan.demo.Produccion.application;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Alacan.demo.Produccion.domain.exception.RecetaNoEncontradaException;
import com.Alacan.demo.Produccion.domain.exception.StockInsuficienteException;
import com.Alacan.demo.Produccion.domain.exception.TipoMovimientoNoEncontradoException;
import com.Alacan.demo.Produccion.domain.model.FaltanteProduccion;
import com.Alacan.demo.Produccion.domain.model.Movimiento;
import com.Alacan.demo.Produccion.domain.model.Receta;
import com.Alacan.demo.Produccion.domain.model.Sentido;
import com.Alacan.demo.Produccion.domain.model.Stock;
import com.Alacan.demo.Produccion.domain.model.TipoMovimiento;
import com.Alacan.demo.Produccion.domain.repository.MovimientoRepository;
import com.Alacan.demo.Produccion.domain.repository.RecetaRepository;
import com.Alacan.demo.Produccion.domain.repository.StockRepository;
import com.Alacan.demo.Produccion.domain.repository.TipoMovimientoRepository;
import com.Alacan.demo.Produccion.domain.service.CalculadorRequerimientosProduccion;
import com.Alacan.demo.Produccion.domain.service.RequerimientoProduccion;

@Service
public class EjecutarProduccionService {

    private static final String TIPO_MOVIMIENTO_PRODUCCION = "PRODUCCION";

    private final RecetaRepository recetaRepository;
    private final StockRepository stockRepository;
    private final MovimientoRepository movimientoRepository;
    private final TipoMovimientoRepository tipoMovimientoRepository;

    public EjecutarProduccionService(RecetaRepository recetaRepository,
            StockRepository stockRepository,
            MovimientoRepository movimientoRepository,
            TipoMovimientoRepository tipoMovimientoRepository) {
        this.recetaRepository = recetaRepository;
        this.stockRepository = stockRepository;
        this.movimientoRepository = movimientoRepository;
        this.tipoMovimientoRepository = tipoMovimientoRepository;
    }

    @Transactional
    public Stock ejecutar(Long recetaId, BigDecimal cantidadAProducir) {

        Receta receta = recetaRepository.buscarPorId(recetaId)
                .orElseThrow(() -> new RecetaNoEncontradaException(recetaId));

        if (!receta.isActivo()) {
            throw new RecetaNoEncontradaException("La receta " + recetaId + " está inactiva, no puede ejecutarse");
        }

        CalculadorRequerimientosProduccion calculador = new CalculadorRequerimientosProduccion(stockRepository);
        List<RequerimientoProduccion> requerimientos = calculador.calcular(receta, cantidadAProducir);

        List<FaltanteProduccion> faltantes = requerimientos.stream()
                .filter(r -> !r.esSuficiente())
                .map(RequerimientoProduccion::aFaltante)
                .toList();

        if (!faltantes.isEmpty()) {
            throw new StockInsuficienteException(faltantes);
        }

        TipoMovimiento tipoProduccion = tipoMovimientoRepository.buscarPorNombre(TIPO_MOVIMIENTO_PRODUCCION)
                .orElseThrow(() -> new TipoMovimientoNoEncontradoException(TIPO_MOVIMIENTO_PRODUCCION));

        for (RequerimientoProduccion requerimiento : requerimientos) {
            Stock stockConsumido = requerimiento.getStock();
            stockConsumido.registrarConsumo(requerimiento.getCantidadNecesaria());
            stockRepository.guardar(stockConsumido);

            movimientoRepository.guardar(new Movimiento(
                    requerimiento.getArticulo(),
                    tipoProduccion,
                    Sentido.SALIDA,
                    requerimiento.getCantidadNecesaria()));
        }

        Stock stockProducido = stockRepository.buscarPorArticuloId(receta.getArticuloProducido().getId())
                .orElseThrow(() -> new IllegalStateException(
                        "El artículo " + receta.getArticuloProducido().getId() + " no tiene stock inicializado"));

        stockProducido.registrarIngreso(cantidadAProducir);
        stockRepository.guardar(stockProducido);

        movimientoRepository.guardar(new Movimiento(
                receta.getArticuloProducido(),
                tipoProduccion,
                Sentido.ENTRADA,
                cantidadAProducir));

        return stockProducido;
    }
}
