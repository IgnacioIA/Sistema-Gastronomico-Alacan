package com.Alacan.demo.Pedido.application;

import com.Alacan.demo.Pedido.domain.Pedido;
import com.Alacan.demo.Pedido.domain.PedidoRepository;

public class ModificarCantidad {

    private PedidoRepository pedidoRepository;

    public ModificarCantidad(PedidoRepository pedidoRepository){
        this.pedidoRepository = pedidoRepository;
    }

    public void ejecutar(Long pedidoId, Long itemId, int cantidad) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId)
        .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        pedido.modificarCantidadItem(itemId, cantidad);

        pedidoRepository.guardar(pedido);
    }

    
}
