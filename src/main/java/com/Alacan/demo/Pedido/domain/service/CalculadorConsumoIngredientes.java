package com.Alacan.demo.Pedido.domain.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.Alacan.demo.Catalogo.domain.model.Combo;
import com.Alacan.demo.Catalogo.domain.model.OpcionCombo;
import com.Alacan.demo.Catalogo.domain.model.Producto;
import com.Alacan.demo.Catalogo.domain.model.Receta;
import com.Alacan.demo.Catalogo.domain.repository.ComboRepository;
import com.Alacan.demo.Catalogo.domain.repository.ProductoRepository;
import com.Alacan.demo.Pedido.domain.model.ItemCombo;
import com.Alacan.demo.Pedido.domain.model.ItemPedido;
import com.Alacan.demo.Pedido.domain.model.ItemProducto;
import com.Alacan.demo.Pedido.domain.model.Pedido;

public class CalculadorConsumoIngredientes {

    private ProductoRepository productoRepository;
    private ComboRepository comboRepository;

    public CalculadorConsumoIngredientes(ProductoRepository productoRepository,
                                         ComboRepository comboRepository) {
        this.productoRepository = productoRepository;
        this.comboRepository = comboRepository;
    }

    public Map<Long, BigDecimal> calcular(Pedido pedido) {

        Map<Long, BigDecimal> consumo = new HashMap<>();

        for (ItemPedido item : pedido.getItems()) {

            if (item instanceof ItemProducto ip) {

                procesarProducto(ip.getProductoId(), ip.getCantidad(), consumo);

            } else if (item instanceof ItemCombo ic) {

                Combo combo = comboRepository.buscarPorId(ic.getComboId())
                        .orElseThrow(() -> new IllegalArgumentException("Combo no encontrado"));

                for (OpcionCombo opcion : combo.getOpciones()) {

                    Producto producto = opcion.getProductoBase(); // simplificación

                    procesarProducto(producto.getId(), ic.getCantidad(), consumo);
                }
            }
        }

        return consumo;
    }

    private void procesarProducto(Long productoId, int cantidadItem, Map<Long, BigDecimal> consumo) {

        Producto producto = productoRepository.buscarPorId(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

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
}
