package com.Alacan.demo.Pedido.api.dto;

import java.math.BigDecimal;

public class ItemDTO {

    private Long id;
    private String productoNombre;
    private int cantidad;
    private Long referenciaId; // productoId o comboId
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String tipo; // "PRODUCTO" o "COMBO"

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return productoNombre;
    }

    public void setNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getTipo(){
        return tipo;
    }

    public void setTipo(String tipo){
        this.tipo = tipo;
    }

    public Long getReferenciaId(){
        return referenciaId;
    }

    public void setReferenciaId(Long referenciaId){
        this.referenciaId = referenciaId;
    }
}
