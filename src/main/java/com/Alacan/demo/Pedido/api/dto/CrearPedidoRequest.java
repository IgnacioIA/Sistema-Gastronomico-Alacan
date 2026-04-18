package com.Alacan.demo.Pedido.api.dto;

import com.Alacan.demo.Pedido.domain.CanalPedido;
import com.Alacan.demo.Pedido.domain.TipoPedido;

public class CrearPedidoRequest {

    public TipoPedido tipoPedido;
    public CanalPedido canal;
    public String direccion;
    public Long clienteId;

    public TipoPedido getTipoPedido() {
        return tipoPedido;
    }

    public CanalPedido getCanal() {
        return canal;
    }

    public String getDireccion() {
        return direccion;
    }

    public Long getClienteId() {
        return clienteId;
    }
}