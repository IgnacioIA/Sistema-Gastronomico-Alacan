package com.Alacan.demo.Produccion.domain.repository;

import java.util.List;
import java.util.Optional;

import com.Alacan.demo.Produccion.domain.model.TipoMovimiento;

public interface TipoMovimientoRepository {

    Optional<TipoMovimiento> buscarPorId(Long id);

    Optional<TipoMovimiento> buscarPorNombre(String nombre);

    List<TipoMovimiento> buscarTodos();
}
