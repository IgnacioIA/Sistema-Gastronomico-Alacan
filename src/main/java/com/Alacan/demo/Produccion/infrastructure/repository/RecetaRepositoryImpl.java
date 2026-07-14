package com.Alacan.demo.Produccion.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.Alacan.demo.Produccion.domain.model.Receta;
import com.Alacan.demo.Produccion.domain.repository.RecetaRepository;

@Repository
public class RecetaRepositoryImpl implements RecetaRepository {

    private final RecetaRepositoryJpa recetaRepositoryJpa;

    public RecetaRepositoryImpl(RecetaRepositoryJpa recetaRepositoryJpa) {
        this.recetaRepositoryJpa = recetaRepositoryJpa;
    }

    @Override
    public Optional<Receta> buscarPorId(Long id) {
        return recetaRepositoryJpa.findById(id);
    }

    @Override
    public Optional<Receta> buscarActivaPorArticuloProducido(Long articuloId) {
        return recetaRepositoryJpa.findByArticuloProducidoIdAndActivoTrue(articuloId);
    }

    @Override
    public List<Receta> buscarTodas() {
        return recetaRepositoryJpa.findAll();
    }

    @Override
    public void guardar(Receta receta) {
        recetaRepositoryJpa.save(receta);
    }

    @Override
    public long contarActivasQueRequierenArticulo(Long articuloId) {
        return recetaRepositoryJpa.contarRecetasActivasQueRequierenArticulo(articuloId);
    }
}
