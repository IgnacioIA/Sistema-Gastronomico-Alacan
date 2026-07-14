package com.Alacan.demo.Produccion.domain.exception;

public class UnidadMedidaNoEncontradaException extends ProduccionException {

    public UnidadMedidaNoEncontradaException(Long unidadMedidaId) {
        super("Unidad de medida no encontrada: id=" + unidadMedidaId);
    }
}
