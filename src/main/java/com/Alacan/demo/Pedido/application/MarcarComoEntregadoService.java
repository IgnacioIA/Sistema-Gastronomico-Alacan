package com.Alacan.demo.Pedido.application;

import org.springframework.stereotype.Service;

import com.Alacan.demo.Pedido.domain.model.Pedido;
import com.Alacan.demo.Pedido.domain.repository.PedidoRepository;

@Service
public class MarcarComoEntregadoService {

    private final PedidoRepository pedidoRepository;

    public MarcarComoEntregadoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public void ejecutar(Long pedidoId) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        pedido.marcarComoEntregado();

        pedidoRepository.guardar(pedido);
    }
}
