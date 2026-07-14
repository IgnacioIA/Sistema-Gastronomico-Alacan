package com.Alacan.demo.Produccion.application;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Alacan.demo.Produccion.domain.exception.TipoArticuloNoEncontradoException;
import com.Alacan.demo.Produccion.domain.exception.TipoMovimientoNoEncontradoException;
import com.Alacan.demo.Produccion.domain.exception.UnidadMedidaNoEncontradaException;
import com.Alacan.demo.Produccion.domain.model.Articulo;
import com.Alacan.demo.Produccion.domain.model.Movimiento;
import com.Alacan.demo.Produccion.domain.model.Sentido;
import com.Alacan.demo.Produccion.domain.model.Stock;
import com.Alacan.demo.Produccion.domain.model.TipoArticulo;
import com.Alacan.demo.Produccion.domain.model.TipoMovimiento;
import com.Alacan.demo.Produccion.domain.model.UnidadMedida;
import com.Alacan.demo.Produccion.domain.repository.ArticuloRepository;
import com.Alacan.demo.Produccion.domain.repository.MovimientoRepository;
import com.Alacan.demo.Produccion.domain.repository.StockRepository;
import com.Alacan.demo.Produccion.domain.repository.TipoArticuloRepository;
import com.Alacan.demo.Produccion.domain.repository.TipoMovimientoRepository;
import com.Alacan.demo.Produccion.domain.repository.UnidadMedidaRepository;

@Service
public class CrearIngredienteService {

    private static final String TIPO_ARTICULO_INGREDIENTE = "INGREDIENTE";
    private static final String TIPO_MOVIMIENTO_ALTA = "ALTA";

    private final ArticuloRepository articuloRepository;
    private final TipoArticuloRepository tipoArticuloRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final StockRepository stockRepository;
    private final MovimientoRepository movimientoRepository;
    private final TipoMovimientoRepository tipoMovimientoRepository;

    public CrearIngredienteService(ArticuloRepository articuloRepository,
            TipoArticuloRepository tipoArticuloRepository,
            UnidadMedidaRepository unidadMedidaRepository,
            StockRepository stockRepository,
            MovimientoRepository movimientoRepository,
            TipoMovimientoRepository tipoMovimientoRepository) {
        this.articuloRepository = articuloRepository;
        this.tipoArticuloRepository = tipoArticuloRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.stockRepository = stockRepository;
        this.movimientoRepository = movimientoRepository;
        this.tipoMovimientoRepository = tipoMovimientoRepository;
    }

    @Transactional
    public IngredienteConStockResultado ejecutar(String nombre,
            Long unidadMedidaId,
            String descripcion,
            BigDecimal stockMinimo,
            BigDecimal cantidadInicial) {

        TipoArticulo tipoIngrediente = tipoArticuloRepository.buscarPorNombre(TIPO_ARTICULO_INGREDIENTE)
                .orElseThrow(() -> new TipoArticuloNoEncontradoException(TIPO_ARTICULO_INGREDIENTE));

        UnidadMedida unidadMedida = unidadMedidaRepository.buscarPorId(unidadMedidaId)
                .orElseThrow(() -> new UnidadMedidaNoEncontradaException(unidadMedidaId));

        Articulo articulo = new Articulo(nombre, tipoIngrediente, unidadMedida, descripcion);
        articuloRepository.guardar(articulo);

        BigDecimal minimo = stockMinimo != null ? stockMinimo : BigDecimal.ZERO;
        Stock stock = new Stock(articulo, minimo);

        boolean hayStockInicial = cantidadInicial != null && cantidadInicial.compareTo(BigDecimal.ZERO) > 0;

        if (hayStockInicial) {
            stock.registrarIngreso(cantidadInicial);
        }

        stockRepository.guardar(stock);

        if (hayStockInicial) {
            TipoMovimiento tipoAlta = tipoMovimientoRepository.buscarPorNombre(TIPO_MOVIMIENTO_ALTA)
                    .orElseThrow(() -> new TipoMovimientoNoEncontradoException(TIPO_MOVIMIENTO_ALTA));

            movimientoRepository.guardar(new Movimiento(articulo, tipoAlta, Sentido.ENTRADA, cantidadInicial));
        }

        return new IngredienteConStockResultado(articulo, stock);
    }
}
