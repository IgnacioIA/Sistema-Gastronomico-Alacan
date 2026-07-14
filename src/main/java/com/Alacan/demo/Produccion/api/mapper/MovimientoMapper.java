package com.Alacan.demo.Produccion.api.mapper;

import com.Alacan.demo.Produccion.api.dto.MovimientoResponse;
import com.Alacan.demo.Produccion.api.dto.PaginaResponse;
import com.Alacan.demo.Produccion.api.dto.TipoMovimientoResponse;
import com.Alacan.demo.Produccion.domain.model.Movimiento;
import com.Alacan.demo.Produccion.domain.query.ResultadoPaginado;

public class MovimientoMapper {

    private MovimientoMapper() {
    }

    public static PaginaResponse<MovimientoResponse> toPaginaResponse(ResultadoPaginado<Movimiento> resultado) {

        PaginaResponse<MovimientoResponse> pagina = new PaginaResponse<>();
        pagina.setContent(resultado.getContent().stream().map(MovimientoMapper::toResponse).toList());
        pagina.setPage(resultado.getPage());
        pagina.setSize(resultado.getSize());
        pagina.setTotalElements(resultado.getTotalElements());
        pagina.setTotalPages(resultado.getTotalPages());
        pagina.setFirst(resultado.isFirst());
        pagina.setLast(resultado.isLast());
        pagina.setEmpty(resultado.isEmpty());

        return pagina;
    }

    private static MovimientoResponse toResponse(Movimiento movimiento) {

        MovimientoResponse response = new MovimientoResponse();
        response.setId(movimiento.getId());
        response.setSentido(movimiento.getSentido().name());
        response.setCantidad(movimiento.getCantidad());
        response.setMomento(movimiento.getMomento());

        TipoMovimientoResponse tipoMovimiento = new TipoMovimientoResponse();
        tipoMovimiento.setId(movimiento.getTipoMovimiento().getId());
        tipoMovimiento.setNombre(movimiento.getTipoMovimiento().getNombre());
        response.setTipoMovimiento(tipoMovimiento);

        return response;
    }
}
