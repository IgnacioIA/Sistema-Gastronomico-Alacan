package com.Alacan.demo.Produccion.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.Alacan.demo.Produccion.domain.model.UnidadMedida;
import com.Alacan.demo.Produccion.domain.repository.UnidadMedidaRepository;

@Repository
public class UnidadMedidaRepositoryImpl implements UnidadMedidaRepository {

    private final UnidadMedidaRepositoryJpa unidadMedidaRepositoryJpa;

    public UnidadMedidaRepositoryImpl(UnidadMedidaRepositoryJpa unidadMedidaRepositoryJpa) {
        this.unidadMedidaRepositoryJpa = unidadMedidaRepositoryJpa;
    }

    @Override
    public Optional<UnidadMedida> buscarPorId(Long id) {
        return unidadMedidaRepositoryJpa.findById(id);
    }

    @Override
    public List<UnidadMedida> buscarTodas() {
        return unidadMedidaRepositoryJpa.findAll();
    }
}
