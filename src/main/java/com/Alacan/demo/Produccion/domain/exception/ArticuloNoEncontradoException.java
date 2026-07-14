package com.Alacan.demo.Produccion.domain.exception;

public class ArticuloNoEncontradoException extends ProduccionException {

    public ArticuloNoEncontradoException(Long articuloId) {
        super("Artículo no encontrado: id=" + articuloId);
    }
}
