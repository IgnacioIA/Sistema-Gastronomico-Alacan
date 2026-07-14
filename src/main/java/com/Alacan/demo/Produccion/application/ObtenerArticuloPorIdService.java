package com.Alacan.demo.Produccion.application;

import org.springframework.stereotype.Service;

import com.Alacan.demo.Produccion.domain.exception.ArticuloNoEncontradoException;
import com.Alacan.demo.Produccion.domain.model.Articulo;
import com.Alacan.demo.Produccion.domain.repository.ArticuloRepository;

@Service
public class ObtenerArticuloPorIdService {

    private final ArticuloRepository articuloRepository;

    public ObtenerArticuloPorIdService(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    public Articulo ejecutar(Long articuloId) {
        return articuloRepository.buscarPorId(articuloId)
                .orElseThrow(() -> new ArticuloNoEncontradoException(articuloId));
    }
}
