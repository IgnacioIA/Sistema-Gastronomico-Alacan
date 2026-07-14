package com.Alacan.demo.Produccion.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Alacan.demo.Produccion.api.dto.UnidadMedidaResponse;
import com.Alacan.demo.Produccion.api.mapper.UnidadMedidaMapper;
import com.Alacan.demo.Produccion.application.ListarUnidadesMedidaService;

// Recurso propio, no anidado bajo /ingredients: UnidadMedida es un atributo de Articulo
// en general (ADR 0001, seccion 12), reutilizable por cualquier pantalla que cree o
// edite articulos (Preparaciones, Productos, etc.), no exclusivo de Ingredientes.
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/measurement-units")
public class MeasurementUnitController {

    private final ListarUnidadesMedidaService listarUnidadesMedidaService;

    public MeasurementUnitController(ListarUnidadesMedidaService listarUnidadesMedidaService) {
        this.listarUnidadesMedidaService = listarUnidadesMedidaService;
    }

    @GetMapping
    public ResponseEntity<List<UnidadMedidaResponse>> listarUnidadesMedida() {

        List<UnidadMedidaResponse> response = listarUnidadesMedidaService.ejecutar().stream()
                .map(UnidadMedidaMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }
}
