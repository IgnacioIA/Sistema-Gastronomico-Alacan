package com.Alacan.demo.Produccion.domain.exception;

public class UnidadMedidaBloqueadaException extends ProduccionException {

    public UnidadMedidaBloqueadaException(Long articuloId) {
        super("El artículo " + articuloId
                + " ya tiene stock físico o movimientos registrados: su unidad de medida no puede modificarse");
    }
}
