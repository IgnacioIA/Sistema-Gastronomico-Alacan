package com.Alacan.demo.Produccion.api.mapper;

import com.Alacan.demo.Produccion.api.dto.UnidadMedidaResponse;
import com.Alacan.demo.Produccion.domain.model.UnidadMedida;

public class UnidadMedidaMapper {

    private UnidadMedidaMapper() {
    }

    public static UnidadMedidaResponse toResponse(UnidadMedida unidadMedida) {

        UnidadMedidaResponse response = new UnidadMedidaResponse();
        response.setId(unidadMedida.getId());
        response.setNombre(unidadMedida.getNombre());
        response.setAbreviatura(unidadMedida.getAbreviatura());

        return response;
    }
}
