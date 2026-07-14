package com.Alacan.demo.Produccion.application;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Alacan.demo.Produccion.domain.exception.ArticuloNoEncontradoException;
import com.Alacan.demo.Produccion.domain.exception.UnidadMedidaBloqueadaException;
import com.Alacan.demo.Produccion.domain.exception.UnidadMedidaNoEncontradaException;
import com.Alacan.demo.Produccion.domain.model.Articulo;
import com.Alacan.demo.Produccion.domain.model.Stock;
import com.Alacan.demo.Produccion.domain.model.UnidadMedida;
import com.Alacan.demo.Produccion.domain.repository.ArticuloRepository;
import com.Alacan.demo.Produccion.domain.repository.MovimientoRepository;
import com.Alacan.demo.Produccion.domain.repository.StockRepository;
import com.Alacan.demo.Produccion.domain.repository.UnidadMedidaRepository;

@Service
public class ActualizarIngredienteService {

    private final ArticuloRepository articuloRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final StockRepository stockRepository;
    private final MovimientoRepository movimientoRepository;

    public ActualizarIngredienteService(ArticuloRepository articuloRepository,
            UnidadMedidaRepository unidadMedidaRepository,
            StockRepository stockRepository,
            MovimientoRepository movimientoRepository) {
        this.articuloRepository = articuloRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.stockRepository = stockRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional
    public IngredienteConStockResultado ejecutar(Long articuloId,
            String nombre,
            String descripcion,
            Long unidadMedidaId,
            BigDecimal stockMinimo) {

        Articulo articulo = articuloRepository.buscarPorId(articuloId)
                .orElseThrow(() -> new ArticuloNoEncontradoException(articuloId));

        Stock stock = stockRepository.buscarPorArticuloId(articuloId)
                .orElseThrow(() -> new IllegalStateException(
                        "El artículo " + articuloId + " no tiene stock inicializado"));

        articulo.modificarNombre(nombre);
        articulo.modificarDescripcion(descripcion);

        boolean cambiaUnidad = !articulo.getUnidadMedida().getId().equals(unidadMedidaId);

        if (cambiaUnidad) {
            validarCambioDeUnidadPermitido(articuloId, stock);

            UnidadMedida nuevaUnidadMedida = unidadMedidaRepository.buscarPorId(unidadMedidaId)
                    .orElseThrow(() -> new UnidadMedidaNoEncontradaException(unidadMedidaId));

            articulo.modificarUnidadMedida(nuevaUnidadMedida);
        }

        stock.modificarStockMinimo(stockMinimo);

        articuloRepository.guardar(articulo);
        stockRepository.guardar(stock);

        return new IngredienteConStockResultado(articulo, stock);
    }

    // La unidad de medida solo puede cambiarse mientras el artículo no tenga stock
    // físico ni movimientos registrados (ADR 0001, sección 16).
    private void validarCambioDeUnidadPermitido(Long articuloId, Stock stock) {

        boolean tieneStockFisico = stock.getCantidadActual().compareTo(BigDecimal.ZERO) != 0;
        boolean tieneMovimientos = !movimientoRepository.buscarPorArticuloId(articuloId).isEmpty();

        if (tieneStockFisico || tieneMovimientos) {
            throw new UnidadMedidaBloqueadaException(articuloId);
        }
    }
}
