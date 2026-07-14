package com.Alacan.demo.Produccion.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.Alacan.demo.Produccion.domain.model.TipoArticulo;
import com.Alacan.demo.Produccion.domain.repository.TipoArticuloRepository;

@Repository
public class TipoArticuloRepositoryImpl implements TipoArticuloRepository {

    private final TipoArticuloRepositoryJpa tipoArticuloRepositoryJpa;

    public TipoArticuloRepositoryImpl(TipoArticuloRepositoryJpa tipoArticuloRepositoryJpa) {
        this.tipoArticuloRepositoryJpa = tipoArticuloRepositoryJpa;
    }

    @Override
    public Optional<TipoArticulo> buscarPorId(Long id) {
        return tipoArticuloRepositoryJpa.findById(id);
    }

    @Override
    public Optional<TipoArticulo> buscarPorNombre(String nombre) {
        return tipoArticuloRepositoryJpa.findByNombre(nombre);
    }

    @Override
    public List<TipoArticulo> buscarTodos() {
        return tipoArticuloRepositoryJpa.findAll();
    }
}
