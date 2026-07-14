package com.Alacan.demo.Produccion.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Alacan.demo.Produccion.domain.model.Articulo;
import com.Alacan.demo.Produccion.domain.repository.ArticuloRepository;

@Service
public class ListarArticulosService {

    private final ArticuloRepository articuloRepository;

    public ListarArticulosService(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    public List<Articulo> ejecutar() {
        return articuloRepository.buscarTodos();
    }
}
