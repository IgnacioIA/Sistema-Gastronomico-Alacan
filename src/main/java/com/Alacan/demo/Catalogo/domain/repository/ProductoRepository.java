package com.Alacan.demo.Catalogo.domain.repository;

import java.util.List;
import java.util.Optional;

import com.Alacan.demo.Catalogo.domain.model.Producto;

public interface  ProductoRepository {
    Optional<Producto> buscarPorId(Long id);

    List<Producto> buscarTodos();

    void guardar(Producto producto);
}
