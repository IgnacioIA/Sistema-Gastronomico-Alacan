package com.Alacan.demo.Produccion.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Alacan.demo.Produccion.domain.model.UnidadMedida;
import com.Alacan.demo.Produccion.domain.repository.UnidadMedidaRepository;

// Catalogo de referencia (ADR 0001, seccion 12): se devuelve completo, sin paginar
// (ver INTEGRATION_GUIDE.md - "Catalogos de referencia").
@Service
public class ListarUnidadesMedidaService {

    private final UnidadMedidaRepository unidadMedidaRepository;

    public ListarUnidadesMedidaService(UnidadMedidaRepository unidadMedidaRepository) {
        this.unidadMedidaRepository = unidadMedidaRepository;
    }

    public List<UnidadMedida> ejecutar() {
        return unidadMedidaRepository.buscarTodas();
    }
}
