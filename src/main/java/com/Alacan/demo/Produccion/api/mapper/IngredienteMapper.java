package com.Alacan.demo.Produccion.api.mapper;

import com.Alacan.demo.Produccion.api.dto.DesactivarIngredienteResponse;
import com.Alacan.demo.Produccion.api.dto.IngredienteListItemResponse;
import com.Alacan.demo.Produccion.api.dto.IngredienteResponse;
import com.Alacan.demo.Produccion.api.dto.PaginaResponse;
import com.Alacan.demo.Produccion.api.dto.StockResponse;
import com.Alacan.demo.Produccion.api.dto.TipoArticuloResponse;
import com.Alacan.demo.Produccion.application.DesactivarIngredienteResultado;
import com.Alacan.demo.Produccion.application.IngredienteConStockResultado;
import com.Alacan.demo.Produccion.domain.model.Articulo;
import com.Alacan.demo.Produccion.domain.model.Stock;
import com.Alacan.demo.Produccion.domain.query.ArticuloConStock;
import com.Alacan.demo.Produccion.domain.query.ResultadoPaginado;

public class IngredienteMapper {

    private IngredienteMapper() {
    }

    public static IngredienteResponse toResponse(IngredienteConStockResultado resultado) {
        return toResponse(resultado.getArticulo(), resultado.getStock());
    }

    public static IngredienteResponse toResponse(Articulo articulo, Stock stock) {

        IngredienteResponse response = new IngredienteResponse();
        response.setId(articulo.getId());
        response.setNombre(articulo.getNombre());
        response.setDescripcion(articulo.getDescripcion());
        response.setActivo(articulo.isActivo());

        TipoArticuloResponse tipoArticulo = new TipoArticuloResponse();
        tipoArticulo.setId(articulo.getTipoArticulo().getId());
        tipoArticulo.setNombre(articulo.getTipoArticulo().getNombre());
        response.setTipoArticulo(tipoArticulo);

        response.setUnidadMedida(UnidadMedidaMapper.toResponse(articulo.getUnidadMedida()));
        response.setStock(toStockResponse(stock));

        return response;
    }

    public static DesactivarIngredienteResponse toResponse(DesactivarIngredienteResultado resultado) {

        DesactivarIngredienteResponse response = new DesactivarIngredienteResponse();
        response.setIngrediente(toResponse(resultado.getArticulo(), resultado.getStock()));
        response.setRecetasActivasQueLoUtilizan(resultado.getRecetasActivasQueLoUtilizan());

        return response;
    }

    public static PaginaResponse<IngredienteListItemResponse> toPaginaResponse(ResultadoPaginado<ArticuloConStock> resultado) {

        PaginaResponse<IngredienteListItemResponse> pagina = new PaginaResponse<>();
        pagina.setContent(resultado.getContent().stream().map(IngredienteMapper::toListItem).toList());
        pagina.setPage(resultado.getPage());
        pagina.setSize(resultado.getSize());
        pagina.setTotalElements(resultado.getTotalElements());
        pagina.setTotalPages(resultado.getTotalPages());
        pagina.setFirst(resultado.isFirst());
        pagina.setLast(resultado.isLast());
        pagina.setEmpty(resultado.isEmpty());

        return pagina;
    }

    private static IngredienteListItemResponse toListItem(ArticuloConStock articuloConStock) {

        Articulo articulo = articuloConStock.getArticulo();

        IngredienteListItemResponse item = new IngredienteListItemResponse();
        item.setId(articulo.getId());
        item.setNombre(articulo.getNombre());
        item.setDescripcion(articulo.getDescripcion());
        item.setActivo(articulo.isActivo());
        item.setUnidadMedida(UnidadMedidaMapper.toResponse(articulo.getUnidadMedida()));
        item.setStock(toStockResponse(articuloConStock.getStock()));

        return item;
    }

    private static StockResponse toStockResponse(Stock stock) {

        StockResponse response = new StockResponse();
        response.setCantidadActual(stock.getCantidadActual());
        response.setCantidadReservada(stock.getCantidadReservada());
        response.setCantidadDisponible(stock.cantidadDisponible());
        response.setStockMinimo(stock.getStockMinimo());
        response.setNecesitaReposicion(stock.necesitaReposicion());

        return response;
    }
}
