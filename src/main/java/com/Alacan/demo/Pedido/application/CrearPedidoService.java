package com.Alacan.demo.Pedido.application;

import org.springframework.stereotype.Service;

import com.Alacan.demo.Pedido.api.dto.CrearPedidoRequest;
import com.Alacan.demo.Pedido.domain.Pedido;
import com.Alacan.demo.Pedido.domain.PedidoRepository;

@Service
public class CrearPedidoService {

    private final PedidoRepository pedidoRepository;

    public CrearPedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public Long ejecutar(CrearPedidoRequest request){

        Pedido pedido = new Pedido(
            request.getTipoPedido(),
            request.getCanal(),
            request.getDireccion(),
            request.getClienteId()
        );

        return pedidoRepository.guardar(pedido).getId();
    }
}