package com.Alacan.demo.Produccion.domain.repository;

import java.util.List;
import java.util.Optional;

import com.Alacan.demo.Produccion.domain.model.UnidadMedida;

public interface UnidadMedidaRepository {

    Optional<UnidadMedida> buscarPorId(Long id);

    List<UnidadMedida> buscarTodas();
}
