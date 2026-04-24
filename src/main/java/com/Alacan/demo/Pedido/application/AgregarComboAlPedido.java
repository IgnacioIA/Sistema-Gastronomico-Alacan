package com.Alacan.demo.Pedido.application;

import org.springframework.stereotype.Service;

import com.Alacan.demo.Catalogo.domain.model.Combo;
import com.Alacan.demo.Catalogo.domain.repository.ComboRepository;
import com.Alacan.demo.Pedido.domain.model.Pedido;
import com.Alacan.demo.Pedido.domain.repository.PedidoRepository;

@Service
public class AgregarComboAlPedido {

    private final PedidoRepository pedidoRepository;
    private final ComboRepository comboRepository;

    public AgregarComboAlPedido(PedidoRepository pedidoRepository,
            ComboRepository comboRepository) {
        this.pedidoRepository = pedidoRepository;
        this.comboRepository = comboRepository;
    }

    public void ejecutar(Long pedidoId, Long comboId, int cantidad, String observacion) {

        Pedido pedido = pedidoRepository.buscarPorId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        Combo combo = comboRepository.buscarPorId(comboId)
                .orElseThrow(() -> new IllegalArgumentException("Combo no encontrado"));

        pedido.agregarCombo(combo, cantidad, observacion);

        pedidoRepository.guardar(pedido);
    }

}
