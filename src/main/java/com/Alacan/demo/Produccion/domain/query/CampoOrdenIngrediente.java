package com.Alacan.demo.Produccion.domain.query;

// Campos de ordenamiento aprobados para el listado de Ingredientes. No se generaliza a un
// enum compartido entre recursos: cada listado futuro (recipes, production, stock) tendrá
// sus propios campos ordenables, que no necesariamente coinciden con estos.
public enum CampoOrdenIngrediente {
    NOMBRE,
    CANTIDAD_ACTUAL,
    CANTIDAD_DISPONIBLE,
    STOCK_MINIMO,
    NECESITA_REPOSICION
}
