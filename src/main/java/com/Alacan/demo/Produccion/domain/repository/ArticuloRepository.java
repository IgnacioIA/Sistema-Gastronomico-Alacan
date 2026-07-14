package com.Alacan.demo.Produccion.domain.repository;

import java.util.List;
import java.util.Optional;

import com.Alacan.demo.Produccion.domain.model.Articulo;
import com.Alacan.demo.Produccion.domain.model.TipoArticulo;
import com.Alacan.demo.Produccion.domain.query.ArticuloConStock;
import com.Alacan.demo.Produccion.domain.query.FiltroListaArticulos;
import com.Alacan.demo.Produccion.domain.query.OrdenIngrediente;
import com.Alacan.demo.Produccion.domain.query.Paginacion;
import com.Alacan.demo.Produccion.domain.query.ResultadoPaginado;

public interface ArticuloRepository {

    Optional<Articulo> buscarPorId(Long id);

    List<Articulo> buscarTodos();

    void guardar(Articulo articulo);

    // Proyección de lectura para listados de gestión (cruza Articulo con su Stock).
    // "tipoArticulo" queda como parámetro explícito para que este método sirva, sin
    // cambios, a futuros listados equivalentes (Preparaciones, Productos, etc.).
    ResultadoPaginado<ArticuloConStock> buscarConStockPaginado(
            TipoArticulo tipoArticulo,
            FiltroListaArticulos filtro,
            OrdenIngrediente orden,
            Paginacion paginacion);
}
