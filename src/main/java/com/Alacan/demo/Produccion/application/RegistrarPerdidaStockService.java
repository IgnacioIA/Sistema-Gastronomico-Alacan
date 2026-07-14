package com.Alacan.demo.Produccion.application;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Alacan.demo.Produccion.domain.exception.ArticuloInactivoException;
import com.Alacan.demo.Produccion.domain.exception.ArticuloNoEncontradoException;
import com.Alacan.demo.Produccion.domain.exception.TipoMovimientoNoEncontradoException;
import com.Alacan.demo.Produccion.domain.model.Articulo;
import com.Alacan.demo.Produccion.domain.model.Movimiento;
import com.Alacan.demo.Produccion.domain.model.Sentido;
import com.Alacan.demo.Produccion.domain.model.Stock;
import com.Alacan.demo.Produccion.domain.model.TipoMovimiento;
import com.Alacan.demo.Produccion.domain.repository.ArticuloRepository;
import com.Alacan.demo.Produccion.domain.repository.MovimientoRepository;
import com.Alacan.demo.Produccion.domain.repository.StockRepository;
import com.Alacan.demo.Produccion.domain.repository.TipoMovimientoRepository;

// Caso de uso especifico para "registrar perdida de stock" (ADR 0001, seccion 17):
// el motivo de negocio fija el TipoMovimiento (DESPERDICIO), nunca lo elige el Frontend.
@Service
public class RegistrarPerdidaStockService {

    private static final String TIPO_MOVIMIENTO_DESPERDICIO = "DESPERDICIO";

    private final ArticuloRepository articuloRepository;
    private final StockRepository stockRepository;
    private final MovimientoRepository movimientoRepository;
    private final TipoMovimientoRepository tipoMovimientoRepository;

    public RegistrarPerdidaStockService(ArticuloRepository articuloRepository,
            StockRepository stockRepository,
            MovimientoRepository movimientoRepository,
            TipoMovimientoRepository tipoMovimientoRepository) {
        this.articuloRepository = articuloRepository;
        this.stockRepository = stockRepository;
        this.movimientoRepository = movimientoRepository;
        this.tipoMovimientoRepository = tipoMovimientoRepository;
    }

    @Transactional
    public IngredienteConStockResultado ejecutar(Long articuloId, BigDecimal cantidad) {

        Articulo articulo = articuloRepository.buscarPorId(articuloId)
                .orElseThrow(() -> new ArticuloNoEncontradoException(articuloId));

        if (!articulo.isActivo()) {
            throw new ArticuloInactivoException(articuloId);
        }

        Stock stock = stockRepository.buscarPorArticuloId(articuloId)
                .orElseThrow(() -> new IllegalStateException(
                        "El artículo " + articuloId + " no tiene stock inicializado"));

        // Si la cantidad supera el stock disponible, registrarConsumo lanza
        // IllegalStateException ("Stock insuficiente..."), mapeada a 400 VALIDATION_ERROR
        // por el GlobalExceptionHandler - mismo criterio ya usado por RegistrarAjusteStockService.
        stock.registrarConsumo(cantidad);
        stockRepository.guardar(stock);

        TipoMovimiento tipoDesperdicio = tipoMovimientoRepository.buscarPorNombre(TIPO_MOVIMIENTO_DESPERDICIO)
                .orElseThrow(() -> new TipoMovimientoNoEncontradoException(TIPO_MOVIMIENTO_DESPERDICIO));

        movimientoRepository.guardar(new Movimiento(articulo, tipoDesperdicio, Sentido.SALIDA, cantidad));

        return new IngredienteConStockResultado(articulo, stock);
    }
}
