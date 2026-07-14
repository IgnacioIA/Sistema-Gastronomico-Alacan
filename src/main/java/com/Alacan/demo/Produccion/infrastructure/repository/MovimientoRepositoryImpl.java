package com.Alacan.demo.Produccion.infrastructure.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.Alacan.demo.Produccion.domain.model.Movimiento;
import com.Alacan.demo.Produccion.domain.query.Paginacion;
import com.Alacan.demo.Produccion.domain.query.ResultadoPaginado;
import com.Alacan.demo.Produccion.domain.repository.MovimientoRepository;

@Repository
public class MovimientoRepositoryImpl implements MovimientoRepository {

    private final MovimientoRepositoryJpa movimientoRepositoryJpa;

    public MovimientoRepositoryImpl(MovimientoRepositoryJpa movimientoRepositoryJpa) {
        this.movimientoRepositoryJpa = movimientoRepositoryJpa;
    }

    @Override
    public List<Movimiento> buscarPorArticuloId(Long articuloId) {
        return movimientoRepositoryJpa.findByArticuloId(articuloId);
    }

    @Override
    public ResultadoPaginado<Movimiento> buscarPorArticuloIdPaginado(Long articuloId, Paginacion paginacion) {

        // "id" como desempate: dos movimientos pueden compartir el mismo "momento" si se
        // registran dentro del mismo instante, y el orden no debe quedar ambiguo.
        PageRequest pageRequest = PageRequest.of(paginacion.getPage(), paginacion.getSize(),
                Sort.by(Sort.Direction.DESC, "momento").and(Sort.by(Sort.Direction.DESC, "id")));

        Page<Movimiento> pagina = movimientoRepositoryJpa.findByArticuloId(articuloId, pageRequest);

        return new ResultadoPaginado<>(pagina.getContent(), paginacion.getPage(), paginacion.getSize(),
                pagina.getTotalElements());
    }

    @Override
    public void guardar(Movimiento movimiento) {
        movimientoRepositoryJpa.save(movimiento);
    }
}
