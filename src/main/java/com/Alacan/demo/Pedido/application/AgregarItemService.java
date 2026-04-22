package com.Alacan.demo.Pedido.application;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.Alacan.demo.Pedido.domain.model.Pedido;
import com.Alacan.demo.Pedido.domain.repository.PedidoRepository;

@Service
public class AgregarItemService {

    private final PedidoRepository pedidoRepository;

    public AgregarItemService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public void ejecutar(Long pedidoId, Long productoId, String nombre, 
        BigDecimal precio, int cantidad, String observacion) {

        Pedido pedido = pedidoRepository.buscarPorId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        pedido.agregarItem(productoId, nombre, precio, cantidad, observacion);

        pedidoRepository.guardar(pedido);
    }
}