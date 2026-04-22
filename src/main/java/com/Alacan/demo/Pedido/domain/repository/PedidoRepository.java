package com.Alacan.demo.Pedido.domain.repository;

import java.util.List;
import java.util.Optional;

import com.Alacan.demo.Pedido.domain.model.Pedido;

public interface PedidoRepository {

    Pedido guardar(Pedido pedido);

    Optional<Pedido> buscarPorId(Long id);

    List<Pedido> buscarTodos();    
}