package com.Alacan.demo.Produccion.domain.query;

import com.Alacan.demo.Produccion.domain.model.Articulo;
import com.Alacan.demo.Produccion.domain.model.Stock;

// Proyección de solo lectura para listados: combina Articulo y su Stock (agregados
// separados en escritura) en una única fila, tal como la necesita la vista de gestión
// de inventario. No es una entidad JPA ni un agregado — es exclusivamente de lectura.
public class ArticuloConStock {

    private final Articulo articulo;
    private final Stock stock;

    public ArticuloConStock(Articulo articulo, Stock stock) {
        this.articulo = articulo;
        this.stock = stock;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public Stock getStock() {
        return stock;
    }
}
