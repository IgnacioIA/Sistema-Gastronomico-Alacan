package com.Alacan.demo.Produccion.application;

import org.springframework.stereotype.Service;

import com.Alacan.demo.Produccion.domain.exception.TipoArticuloNoEncontradoException;
import com.Alacan.demo.Produccion.domain.model.TipoArticulo;
import com.Alacan.demo.Produccion.domain.query.ArticuloConStock;
import com.Alacan.demo.Produccion.domain.query.FiltroListaArticulos;
import com.Alacan.demo.Produccion.domain.query.OrdenIngrediente;
import com.Alacan.demo.Produccion.domain.query.Paginacion;
import com.Alacan.demo.Produccion.domain.query.ResultadoPaginado;
import com.Alacan.demo.Produccion.domain.repository.ArticuloRepository;
import com.Alacan.demo.Produccion.domain.repository.TipoArticuloRepository;

@Service
public class ListarIngredientesService {

    private static final String TIPO_ARTICULO_INGREDIENTE = "INGREDIENTE";

    private final ArticuloRepository articuloRepository;
    private final TipoArticuloRepository tipoArticuloRepository;

    public ListarIngredientesService(ArticuloRepository articuloRepository,
            TipoArticuloRepository tipoArticuloRepository) {
        this.articuloRepository = articuloRepository;
        this.tipoArticuloRepository = tipoArticuloRepository;
    }

    public ResultadoPaginado<ArticuloConStock> ejecutar(FiltroListaArticulos filtro, OrdenIngrediente orden, Paginacion paginacion) {

        TipoArticulo tipoIngrediente = tipoArticuloRepository.buscarPorNombre(TIPO_ARTICULO_INGREDIENTE)
                .orElseThrow(() -> new TipoArticuloNoEncontradoException(TIPO_ARTICULO_INGREDIENTE));

        // "activo" oculta el ingrediente de la operación diaria (ADR 0001, sección 15):
        // si no se pide explícitamente un estado, la vista por defecto es solo activos.
        FiltroListaArticulos filtroEfectivo = filtro.getActivo() != null
                ? filtro
                : new FiltroListaArticulos(filtro.getSearch(), true, filtro.getUnidadMedidaId(), filtro.getFiltroInventario());

        return articuloRepository.buscarConStockPaginado(tipoIngrediente, filtroEfectivo, orden, paginacion);
    }
}
