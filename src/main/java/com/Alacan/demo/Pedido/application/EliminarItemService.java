package com.Alacan.demo.Pedido.application;

import com.Alacan.demo.Pedido.domain.Pedido;
import com.Alacan.demo.Pedido.domain.PedidoRepository;

public class EliminarItemService {

    private PedidoRepository pedidoRepository;

    public EliminarItemService(PedidoRepository pedidoRepository){
        this.pedidoRepository = pedidoRepository;
    }

    public void ejecutar(Long pedidoId, Long itemId) {
        
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        pedido.eliminarItem(itemId);

        pedidoRepository.guardar(pedido);
    }
    
}
