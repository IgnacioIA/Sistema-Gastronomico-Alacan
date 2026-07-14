package com.Alacan.demo.Pedido.application;

import org.springframework.stereotype.Service;

import com.Alacan.demo.Pedido.domain.model.Pedido;
import com.Alacan.demo.Pedido.domain.repository.PedidoRepository;

@Service
public class EliminarItemService {

    private final PedidoRepository pedidoRepository;

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
