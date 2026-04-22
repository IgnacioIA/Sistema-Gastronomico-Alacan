package com.Alacan.demo.Pedido.application;

import java.util.List;

import com.Alacan.demo.Pedido.api.dto.PedidoDTO;
import com.Alacan.demo.Pedido.api.mapper.PedidoMapper;
import com.Alacan.demo.Pedido.domain.repository.PedidoRepository;

public class ListarPedidosService {

    private PedidoRepository pedidoRepository;

    public ListarPedidosService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public List<PedidoDTO> ejecutar() {
        return pedidoRepository.buscarTodos()
                .stream()
                .map(PedidoMapper::toDTO)
                .toList();
    }

}
