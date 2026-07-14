package com.Alacan.demo.Produccion.domain.repository;

import java.util.List;

import com.Alacan.demo.Produccion.domain.model.Movimiento;
import com.Alacan.demo.Produccion.domain.query.Paginacion;
import com.Alacan.demo.Produccion.domain.query.ResultadoPaginado;

public interface MovimientoRepository {

    List<Movimiento> buscarPorArticuloId(Long articuloId);

    // Historial paginado, mas reciente primero. Usado por "Consultar movimientos".
    ResultadoPaginado<Movimiento> buscarPorArticuloIdPaginado(Long articuloId, Paginacion paginacion);

    void guardar(Movimiento movimiento);
}
