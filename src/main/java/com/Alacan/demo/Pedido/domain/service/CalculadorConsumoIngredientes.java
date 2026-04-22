package com.Alacan.demo.Pedido.domain.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.Alacan.demo.Catalogo.domain.model.Producto;
import com.Alacan.demo.Catalogo.domain.model.Receta;
import com.Alacan.demo.Catalogo.domain.repository.ProductoRepository;
import com.Alacan.demo.Pedido.domain.model.ItemPedido;
import com.Alacan.demo.Pedido.domain.model.Pedido;

public class CalculadorConsumoIngredientes {

    private ProductoRepository productoRepository;

    public CalculadorConsumoIngredientes(ProductoRepository productoRepository){
        this.productoRepository=productoRepository;
    }

    public Map<Long, BigDecimal> calcular(Pedido pedido) {

        Map<Long, BigDecimal> consumo = new HashMap<>();

        for (ItemPedido item : pedido.getItems()) {

            Producto producto = productoRepository.buscarPorId(item.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            int cantidadItem = item.getCantidad();

            for (Receta receta : producto.getRecetas()) {

                Long ingredienteId = receta.getIngredienteId();

                BigDecimal cantidadNecesaria = receta.getCantidadNecesaria()
                        .multiply(BigDecimal.valueOf(cantidadItem));

                consumo.merge(
                        ingredienteId,
                        cantidadNecesaria,
                        BigDecimal::add
                );
            }
        }

        return consumo;
    }
}
