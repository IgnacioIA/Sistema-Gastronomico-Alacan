package com.Alacan.demo.Pedido.application;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.Alacan.demo.Catalogo.domain.model.Producto;
import com.Alacan.demo.Catalogo.domain.repository.ProductoRepository;
import com.Alacan.demo.Pedido.domain.model.Pedido;
import com.Alacan.demo.Pedido.domain.repository.PedidoRepository;

@Service
public class AgregarProductoAlPedido {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    public AgregarProductoAlPedido(PedidoRepository pedidoRepository,
            ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }

    public void ejecutar(Long pedidoId, Long productoId, int cantidad, String observacion) {

        Pedido pedido = pedidoRepository.buscarPorId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        Producto producto = productoRepository.buscarPorId(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        pedido.agregarProducto(producto, cantidad, observacion);

        pedidoRepository.guardar(pedido);
    }
}