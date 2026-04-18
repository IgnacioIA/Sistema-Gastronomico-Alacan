package com.Alacan.demo.Pedido.api.mapper;

import com.Alacan.demo.Pedido.api.dto.ItemDTO;
import com.Alacan.demo.Pedido.api.dto.PedidoDTO;
import com.Alacan.demo.Pedido.domain.ItemPedido;
import com.Alacan.demo.Pedido.domain.Pedido;


public class PedidoMapper {

    public static PedidoDTO toDTO(Pedido pedido) {

        if (pedido == null) {
            return null;
        }

        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedido.getId());
        dto.setEstado(pedido.getEstado().name());
        dto.setTotal(pedido.getTotal());

        dto.setItems(
                pedido.getItems()
                        .stream()
                        .map(PedidoMapper::mapItem)
                        .toList()
        );

        return dto;
    }

    private static ItemDTO mapItem(ItemPedido item) {

        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setProductoNombre(item.getNombreProducto()); // asumido
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(item.getPrecioUnitario());
        dto.setSubtotal(item.getSubtotal());

        return dto;
    }
    
}
