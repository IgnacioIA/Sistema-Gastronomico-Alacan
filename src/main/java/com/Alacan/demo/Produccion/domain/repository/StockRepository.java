package com.Alacan.demo.Produccion.domain.repository;

import java.util.Optional;

import com.Alacan.demo.Produccion.domain.model.Stock;

public interface StockRepository {

    Optional<Stock> buscarPorArticuloId(Long articuloId);

    void guardar(Stock stock);
}
