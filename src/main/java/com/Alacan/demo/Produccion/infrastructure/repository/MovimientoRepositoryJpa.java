package com.Alacan.demo.Produccion.infrastructure.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Alacan.demo.Produccion.domain.model.Movimiento;

@Repository
public interface MovimientoRepositoryJpa extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByArticuloId(Long articuloId);

    Page<Movimiento> findByArticuloId(Long articuloId, Pageable pageable);
}
