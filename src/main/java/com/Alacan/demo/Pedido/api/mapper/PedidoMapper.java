package com.Alacan.demo.Pedido.api.mapper;

import com.Alacan.demo.Pedido.api.dto.ItemDTO;
import com.Alacan.demo.Pedido.api.dto.PedidoDTO;
import com.Alacan.demo.Pedido.domain.model.ItemCombo;
import com.Alacan.demo.Pedido.domain.model.ItemPedido;
import com.Alacan.demo.Pedido.domain.model.ItemProducto;
import com.Alacan.demo.Pedido.domain.model.Pedido;

public class PedidoMapper {

    public static PedidoDTO toDTO(Pedido pedido) {

        if (pedido == null) {
            return null;
        }

        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedido.getId());
        dto.setEstado(pedido.getEstado().name());
        dto.setTipoPedido(pedido.getTipoPedido().name());
        dto.setCanal(pedido.getCanal().name());
        dto.setClienteId(pedido.getClienteId());
        dto.setDireccion(pedido.getDireccion());
        dto.setFechaCreacion(pedido.getFechaCreacion());
        dto.setTotal(pedido.getTotal());
        dto.setItems(pedido.getItems().stream().map(PedidoMapper::mapItem).toList());

        return dto;
    }

    private static ItemDTO mapItem(ItemPedido item) {

        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setCantidad(item.getCantidad());

        if (item instanceof ItemProducto ip) {
            dto.setTipo("PRODUCTO");
            dto.setNombre(ip.getNombreItemProducto());
            dto.setReferenciaId(ip.getProductoId());
            dto.setPrecioUnitario(ip.getPrecioUnitario());
            dto.setSubtotal(ip.calcularSubtotal());
        } else if (item instanceof ItemCombo ic) {
            dto.setTipo("COMBO");
            dto.setNombre(ic.getNombreCombo());
            dto.setReferenciaId(ic.getComboId());
            dto.setPrecioUnitario(ic.getPrecioCombo());
            dto.setSubtotal(ic.calcularSubtotal());
        }

        return dto;
    }

}
