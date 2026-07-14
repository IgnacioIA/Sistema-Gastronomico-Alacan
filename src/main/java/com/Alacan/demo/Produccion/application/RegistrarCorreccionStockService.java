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

// Caso de uso especifico para "registrar correccion de stock" (ADR 0001, seccion 17).
// A diferencia de ingreso/perdida, la correccion es bidireccional: el usuario informa
// la cantidad real contada, no si quiere sumar o restar - esa decision (sentido) la
// calcula el Backend comparando contra el stock actual. TipoMovimiento siempre AJUSTE.
@Service
public class RegistrarCorreccionStockService {

    private static final String TIPO_MOVIMIENTO_AJUSTE = "AJUSTE";

    private final ArticuloRepository articuloRepository;
    private final StockRepository stockRepository;
    private final MovimientoRepository movimientoRepository;
    private final TipoMovimientoRepository tipoMovimientoRepository;

    public RegistrarCorreccionStockService(ArticuloRepository articuloRepository,
            StockRepository stockRepository,
            MovimientoRepository movimientoRepository,
            TipoMovimientoRepository tipoMovimientoRepository) {
        this.articuloRepository = articuloRepository;
        this.stockRepository = stockRepository;
        this.movimientoRepository = movimientoRepository;
        this.tipoMovimientoRepository = tipoMovimientoRepository;
    }

    @Transactional
    public IngredienteConStockResultado ejecutar(Long articuloId, BigDecimal cantidadReal) {

        if (cantidadReal == null || cantidadReal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La cantidad real no puede ser negativa");
        }

        Articulo articulo = articuloRepository.buscarPorId(articuloId)
                .orElseThrow(() -> new ArticuloNoEncontradoException(articuloId));

        if (!articulo.isActivo()) {
            throw new ArticuloInactivoException(articuloId);
        }

        Stock stock = stockRepository.buscarPorArticuloId(articuloId)
                .orElseThrow(() -> new IllegalStateException(
                        "El artículo " + articuloId + " no tiene stock inicializado"));

        BigDecimal diferencia = cantidadReal.subtract(stock.getCantidadActual());

        if (diferencia.compareTo(BigDecimal.ZERO) == 0) {
            // No hay hecho que registrar: un Movimiento representa un cambio real (contrato
            // 5.6), y acá no hubo ninguno.
            return new IngredienteConStockResultado(articulo, stock);
        }

        Sentido sentido = diferencia.compareTo(BigDecimal.ZERO) > 0 ? Sentido.ENTRADA : Sentido.SALIDA;
        BigDecimal magnitud = diferencia.abs();

        if (sentido == Sentido.ENTRADA) {
            stock.registrarIngreso(magnitud);
        } else {
            stock.registrarConsumo(magnitud);
        }

        stockRepository.guardar(stock);

        TipoMovimiento tipoAjuste = tipoMovimientoRepository.buscarPorNombre(TIPO_MOVIMIENTO_AJUSTE)
                .orElseThrow(() -> new TipoMovimientoNoEncontradoException(TIPO_MOVIMIENTO_AJUSTE));

        movimientoRepository.guardar(new Movimiento(articulo, tipoAjuste, sentido, magnitud));

        return new IngredienteConStockResultado(articulo, stock);
    }
}
