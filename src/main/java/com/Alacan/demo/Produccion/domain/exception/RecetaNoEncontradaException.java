package com.Alacan.demo.Produccion.domain.exception;

public class RecetaNoEncontradaException extends ProduccionException {

    public RecetaNoEncontradaException(Long recetaId) {
        super("Receta no encontrada: id=" + recetaId);
    }

    public RecetaNoEncontradaException(String message) {
        super(message);
    }
}
