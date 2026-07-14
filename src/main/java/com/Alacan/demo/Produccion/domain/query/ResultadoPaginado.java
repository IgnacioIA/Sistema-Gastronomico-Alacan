package com.Alacan.demo.Produccion.domain.query;

import java.util.Collections;
import java.util.List;

public class ResultadoPaginado<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;

    public ResultadoPaginado(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
    }

    public List<T> getContent() {
        return Collections.unmodifiableList(content);
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
    }

    public boolean isFirst() {
        return page == 0;
    }

    public boolean isLast() {
        return page >= getTotalPages() - 1;
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }
}
