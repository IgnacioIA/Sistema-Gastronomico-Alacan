package com.Alacan.demo.Produccion.domain.exception;

public class TipoMovimientoNoEncontradoException extends ProduccionException {

    public TipoMovimientoNoEncontradoException(String nombre) {
        super("Tipo de movimiento no encontrado: nombre=" + nombre);
    }
}
