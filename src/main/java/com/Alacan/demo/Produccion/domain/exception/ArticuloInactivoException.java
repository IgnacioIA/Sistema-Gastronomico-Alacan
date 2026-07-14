package com.Alacan.demo.Produccion.domain.exception;

public class ArticuloInactivoException extends ProduccionException {

    public ArticuloInactivoException(Long articuloId) {
        super("El artículo " + articuloId + " está desactivado: no admite nuevas operaciones de inventario");
    }
}
