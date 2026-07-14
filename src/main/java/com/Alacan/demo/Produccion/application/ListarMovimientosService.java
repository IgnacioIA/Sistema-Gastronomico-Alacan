package com.Alacan.demo.Produccion.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Alacan.demo.Produccion.domain.exception.ArticuloNoEncontradoException;
import com.Alacan.demo.Produccion.domain.model.Movimiento;
import com.Alacan.demo.Produccion.domain.query.Paginacion;
import com.Alacan.demo.Produccion.domain.query.ResultadoPaginado;
import com.Alacan.demo.Produccion.domain.repository.ArticuloRepository;
import com.Alacan.demo.Produccion.domain.repository.MovimientoRepository;

@Service
public class ListarMovimientosService {

    private final ArticuloRepository articuloRepository;
    private final MovimientoRepository movimientoRepository;

    public ListarMovimientosService(ArticuloRepository articuloRepository, MovimientoRepository movimientoRepository) {
        this.articuloRepository = articuloRepository;
        this.movimientoRepository = movimientoRepository;
    }

    public List<Movimiento> ejecutar(Long articuloId) {
        validarArticuloExiste(articuloId);
        return movimientoRepository.buscarPorArticuloId(articuloId);
    }

    public ResultadoPaginado<Movimiento> ejecutar(Long articuloId, Paginacion paginacion) {
        validarArticuloExiste(articuloId);
        return movimientoRepository.buscarPorArticuloIdPaginado(articuloId, paginacion);
    }

    private void validarArticuloExiste(Long articuloId) {
        if (articuloRepository.buscarPorId(articuloId).isEmpty()) {
            throw new ArticuloNoEncontradoException(articuloId);
        }
    }
}
