package com.Alacan.demo.Pedido.api.controller;

import org.springframework.web.bind.annotation.*;

import com.Alacan.demo.Pedido.api.dto.CrearPedidoRequest;
import com.Alacan.demo.Pedido.application.CrearPedidoService;

import org.springframework.http.ResponseEntity;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final CrearPedidoService crearPedidoService;

    public PedidoController(CrearPedidoService crearPedidoService) {
        this.crearPedidoService = crearPedidoService;
    }

    @PostMapping
    public ResponseEntity<Long> crearPedido(@RequestBody CrearPedidoRequest request) {

        Long id = crearPedidoService.ejecutar(request);

        return ResponseEntity.ok(id);
    }
}
