package com.Alacan.demo.Pedido.api.dto;

public class AgregarProductoRequest {
    private Long productoId;
    private int cantidad;
    private String observacion;

    public Long getProductoId(){
        return productoId;
    }

    public int getCantidad(){
        return cantidad;
    }
    
    public String getObservacion(){
        return observacion;
    }
}
