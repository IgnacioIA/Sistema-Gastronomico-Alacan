package com.Alacan.demo.Produccion.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Alacan.demo.Produccion.domain.model.Stock;

@Repository
public interface StockRepositoryJpa extends JpaRepository<Stock, Long> {

    Optional<Stock> findByArticuloId(Long articuloId);
}
