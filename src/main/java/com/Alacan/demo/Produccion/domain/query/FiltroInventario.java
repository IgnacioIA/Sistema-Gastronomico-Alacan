package com.Alacan.demo.Produccion.domain.query;

// Partición de estados de inventario, sin solapamiento entre valores:
// SIN_STOCK: cantidadActual = 0
// NECESITA_REPOSICION: 0 < cantidadActual < stockMinimo
// CON_STOCK: cantidadActual >= stockMinimo
public enum FiltroInventario {
    SIN_STOCK,
    NECESITA_REPOSICION,
    CON_STOCK
}
