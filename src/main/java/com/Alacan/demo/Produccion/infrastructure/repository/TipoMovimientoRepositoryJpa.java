package com.Alacan.demo.Produccion.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Alacan.demo.Produccion.domain.model.TipoMovimiento;

@Repository
public interface TipoMovimientoRepositoryJpa extends JpaRepository<TipoMovimiento, Long> {

    Optional<TipoMovimiento> findByNombre(String nombre);
}
