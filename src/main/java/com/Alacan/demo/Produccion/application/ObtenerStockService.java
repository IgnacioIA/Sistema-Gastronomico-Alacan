package com.Alacan.demo.Produccion.application;

import org.springframework.stereotype.Service;

import com.Alacan.demo.Produccion.domain.exception.ArticuloNoEncontradoException;
import com.Alacan.demo.Produccion.domain.model.Stock;
import com.Alacan.demo.Produccion.domain.repository.ArticuloRepository;
import com.Alacan.demo.Produccion.domain.repository.StockRepository;

@Service
public class ObtenerStockService {

    private final ArticuloRepository articuloRepository;
    private final StockRepository stockRepository;

    public ObtenerStockService(ArticuloRepository articuloRepository, StockRepository stockRepository) {
        this.articuloRepository = articuloRepository;
        this.stockRepository = stockRepository;
    }

    public Stock ejecutar(Long articuloId) {

        if (articuloRepository.buscarPorId(articuloId).isEmpty()) {
            throw new ArticuloNoEncontradoException(articuloId);
        }

        return stockRepository.buscarPorArticuloId(articuloId)
                .orElseThrow(() -> new IllegalStateException(
                        "El artículo " + articuloId + " no tiene stock inicializado"));
    }
}
