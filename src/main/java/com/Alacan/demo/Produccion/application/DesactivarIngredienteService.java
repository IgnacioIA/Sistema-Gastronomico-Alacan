package com.Alacan.demo.Produccion.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Alacan.demo.Produccion.domain.exception.ArticuloNoEncontradoException;
import com.Alacan.demo.Produccion.domain.model.Articulo;
import com.Alacan.demo.Produccion.domain.model.Stock;
import com.Alacan.demo.Produccion.domain.repository.ArticuloRepository;
import com.Alacan.demo.Produccion.domain.repository.RecetaRepository;
import com.Alacan.demo.Produccion.domain.repository.StockRepository;

@Service
public class DesactivarIngredienteService {

    private final ArticuloRepository articuloRepository;
    private final StockRepository stockRepository;
    private final RecetaRepository recetaRepository;

    public DesactivarIngredienteService(ArticuloRepository articuloRepository,
            StockRepository stockRepository,
            RecetaRepository recetaRepository) {
        this.articuloRepository = articuloRepository;
        this.stockRepository = stockRepository;
        this.recetaRepository = recetaRepository;
    }

    @Transactional
    public DesactivarIngredienteResultado ejecutar(Long articuloId) {

        Articulo articulo = articuloRepository.buscarPorId(articuloId)
                .orElseThrow(() -> new ArticuloNoEncontradoException(articuloId));

        Stock stock = stockRepository.buscarPorArticuloId(articuloId)
                .orElseThrow(() -> new IllegalStateException(
                        "El artículo " + articuloId + " no tiene stock inicializado"));

        // Se desactiva siempre, tenga o no referencias activas (recetas, movimientos):
        // la trazabilidad se preserva completa. El conteo es solo informativo para el
        // Frontend (ADR 0001, sección 15).
        articulo.desactivar();
        articuloRepository.guardar(articulo);

        long recetasActivasQueLoUtilizan = recetaRepository.contarActivasQueRequierenArticulo(articuloId);

        return new DesactivarIngredienteResultado(articulo, stock, recetasActivasQueLoUtilizan);
    }
}
