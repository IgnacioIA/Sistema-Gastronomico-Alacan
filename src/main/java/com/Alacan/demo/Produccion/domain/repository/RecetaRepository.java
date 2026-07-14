package com.Alacan.demo.Produccion.domain.repository;

import java.util.List;
import java.util.Optional;

import com.Alacan.demo.Produccion.domain.model.Receta;

public interface RecetaRepository {

    Optional<Receta> buscarPorId(Long id);

    Optional<Receta> buscarActivaPorArticuloProducido(Long articuloId);

    List<Receta> buscarTodas();

    void guardar(Receta receta);

    // Cuenta recetas activas que requieren el artículo como insumo (RecetaDetalle).
    // Usado para informar al usuario, no para bloquear operaciones (ver Desactivar Ingrediente).
    long contarActivasQueRequierenArticulo(Long articuloId);
}
