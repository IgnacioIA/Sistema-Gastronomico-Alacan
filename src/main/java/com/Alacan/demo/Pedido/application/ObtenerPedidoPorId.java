package com.Alacan.demo.Pedido.application;

import com.Alacan.demo.Pedido.api.dto.PedidoDTO;
import com.Alacan.demo.Pedido.api.mapper.PedidoMapper;
import com.Alacan.demo.Pedido.domain.Pedido;
import com.Alacan.demo.Pedido.domain.PedidoRepository;

public class ObtenerPedidoPorId {

    private PedidoRepository pedidoRepository;

    public PedidoDTO ejecutar(Long pedidoId) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        return PedidoMapper.toDTO(pedido);
    }

}
