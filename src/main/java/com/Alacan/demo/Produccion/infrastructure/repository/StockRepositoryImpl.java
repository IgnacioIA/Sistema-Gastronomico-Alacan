package com.Alacan.demo.Produccion.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.Alacan.demo.Produccion.domain.model.Stock;
import com.Alacan.demo.Produccion.domain.repository.StockRepository;

@Repository
public class StockRepositoryImpl implements StockRepository {

    private final StockRepositoryJpa stockRepositoryJpa;

    public StockRepositoryImpl(StockRepositoryJpa stockRepositoryJpa) {
        this.stockRepositoryJpa = stockRepositoryJpa;
    }

    @Override
    public Optional<Stock> buscarPorArticuloId(Long articuloId) {
        return stockRepositoryJpa.findByArticuloId(articuloId);
    }

    @Override
    public void guardar(Stock stock) {
        stockRepositoryJpa.save(stock);
    }
}
