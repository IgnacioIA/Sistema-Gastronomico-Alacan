package com.Alacan.demo.Produccion.domain.exception;

public class TipoArticuloNoEncontradoException extends ProduccionException {

    public TipoArticuloNoEncontradoException(Long tipoArticuloId) {
        super("Tipo de artículo no encontrado: id=" + tipoArticuloId);
    }

    public TipoArticuloNoEncontradoException(String nombre) {
        super("Tipo de artículo no encontrado: nombre=" + nombre);
    }
}
