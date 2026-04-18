package com.Alacan.demo.Pedido.api.dto;

import java.math.BigDecimal;
import java.util.List;

public class PedidoDTO {

     Long id;
    String estado;
    BigDecimal total;
    List<ItemDTO> items;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<ItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemDTO> items) {
        this.items = items;
    }
    
}
