package com.Alacan.demo.Produccion.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Alacan.demo.Produccion.domain.model.Receta;
import com.Alacan.demo.Produccion.domain.repository.RecetaRepository;

@Service
public class ListarRecetasService {

    private final RecetaRepository recetaRepository;

    public ListarRecetasService(RecetaRepository recetaRepository) {
        this.recetaRepository = recetaRepository;
    }

    public List<Receta> ejecutar() {
        return recetaRepository.buscarTodas();
    }
}
