package com.Alacan.demo.Produccion.application;

import org.springframework.stereotype.Service;

import com.Alacan.demo.Produccion.domain.exception.RecetaNoEncontradaException;
import com.Alacan.demo.Produccion.domain.model.Receta;
import com.Alacan.demo.Produccion.domain.repository.RecetaRepository;

@Service
public class ObtenerRecetaPorIdService {

    private final RecetaRepository recetaRepository;

    public ObtenerRecetaPorIdService(RecetaRepository recetaRepository) {
        this.recetaRepository = recetaRepository;
    }

    public Receta ejecutar(Long recetaId) {
        return recetaRepository.buscarPorId(recetaId)
                .orElseThrow(() -> new RecetaNoEncontradaException(recetaId));
    }
}
