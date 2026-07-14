package com.Alacan.demo.Produccion.application;

import com.Alacan.demo.Produccion.domain.model.Articulo;
import com.Alacan.demo.Produccion.domain.model.Stock;

// Resultado compartido por los casos de uso de escritura sobre Ingredientes que devuelven
// el artículo y su stock actualizados (crear, actualizar, ingreso/pérdida/corrección de
// stock). Antes de este tipo, cada servicio tenía su propia clase idéntica en forma.
public class IngredienteConStockResultado {

    private final Articulo articulo;
    private final Stock stock;

    public IngredienteConStockResultado(Articulo articulo, Stock stock) {
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
