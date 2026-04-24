package com.Alacan.demo.Pedido.api.dto;

public class AgregarComboRequest {
    private Long comboId;
    private int cantidad;
    private String observacion;

    public Long getComboId(){
        return comboId;
    }

    public int getCantidad(){
        return cantidad;
    }

    public String getObservacion(){
        return observacion;
    }
}
