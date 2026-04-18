package com.Alacan.demo.Pedido.domain;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository {

    Pedido guardar(Pedido pedido);

    Optional<Pedido> buscarPorId(Long id);

    List<Pedido> buscarTodos();    
}