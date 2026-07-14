package com.Alacan.demo.Produccion.application;

import com.Alacan.demo.Produccion.domain.model.Articulo;
import com.Alacan.demo.Produccion.domain.model.Stock;

public class DesactivarIngredienteResultado {

    private final Articulo articulo;
    private final Stock stock;
    private final long recetasActivasQueLoUtilizan;

    public DesactivarIngredienteResultado(Articulo articulo, Stock stock, long recetasActivasQueLoUtilizan) {
        this.articulo = articulo;
        this.stock = stock;
        this.recetasActivasQueLoUtilizan = recetasActivasQueLoUtilizan;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public Stock getStock() {
        return stock;
    }

    public long getRecetasActivasQueLoUtilizan() {
        return recetasActivasQueLoUtilizan;
    }
}
