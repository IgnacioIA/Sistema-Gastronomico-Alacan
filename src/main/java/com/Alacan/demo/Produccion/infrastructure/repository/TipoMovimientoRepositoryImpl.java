package com.Alacan.demo.Produccion.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.Alacan.demo.Produccion.domain.model.TipoMovimiento;
import com.Alacan.demo.Produccion.domain.repository.TipoMovimientoRepository;

@Repository
public class TipoMovimientoRepositoryImpl implements TipoMovimientoRepository {

    private final TipoMovimientoRepositoryJpa tipoMovimientoRepositoryJpa;

    public TipoMovimientoRepositoryImpl(TipoMovimientoRepositoryJpa tipoMovimientoRepositoryJpa) {
        this.tipoMovimientoRepositoryJpa = tipoMovimientoRepositoryJpa;
    }

    @Override
    public Optional<TipoMovimiento> buscarPorId(Long id) {
        return tipoMovimientoRepositoryJpa.findById(id);
    }

    @Override
    public Optional<TipoMovimiento> buscarPorNombre(String nombre) {
        return tipoMovimientoRepositoryJpa.findByNombre(nombre);
    }

    @Override
    public List<TipoMovimiento> buscarTodos() {
        return tipoMovimientoRepositoryJpa.findAll();
    }
}
