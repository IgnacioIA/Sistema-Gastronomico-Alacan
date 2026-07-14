package com.Alacan.demo.Produccion.domain.query;

public class Paginacion {

    private final int page;
    private final int size;

    public Paginacion(int page, int size) {

        if (page < 0) {
            throw new IllegalArgumentException("El número de página no puede ser negativo");
        }

        if (size < 1) {
            throw new IllegalArgumentException("El tamaño de página debe ser al menos 1");
        }

        this.page = page;
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public int offset() {
        return page * size;
    }
}
