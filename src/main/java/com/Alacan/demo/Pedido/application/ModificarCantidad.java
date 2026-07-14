package com.Alacan.demo.Pedido.application;

import org.springframework.stereotype.Service;

import com.Alacan.demo.Pedido.domain.model.Pedido;
import com.Alacan.demo.Pedido.domain.repository.PedidoRepository;

@Service
public class ModificarCantidad {

    private final PedidoRepository pedidoRepository;

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
