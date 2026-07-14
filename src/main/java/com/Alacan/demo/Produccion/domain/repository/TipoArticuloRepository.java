package com.Alacan.demo.Produccion.domain.repository;

import java.util.List;
import java.util.Optional;

import com.Alacan.demo.Produccion.domain.model.TipoArticulo;

public interface TipoArticuloRepository {

    Optional<TipoArticulo> buscarPorId(Long id);

    Optional<TipoArticulo> buscarPorNombre(String nombre);

    List<TipoArticulo> buscarTodos();
}
