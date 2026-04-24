package com.Alacan.demo.Pedido.api.controller;

import org.springframework.web.bind.annotation.*;

import com.Alacan.demo.Pedido.api.dto.AgregarComboRequest;
import com.Alacan.demo.Pedido.api.dto.AgregarProductoRequest;
import com.Alacan.demo.Pedido.api.dto.CrearPedidoRequest;
import com.Alacan.demo.Pedido.api.dto.PedidoDTO;
import com.Alacan.demo.Pedido.application.AgregarComboAlPedido;
import com.Alacan.demo.Pedido.application.AgregarProductoAlPedido;
import com.Alacan.demo.Pedido.application.CrearPedidoService;
import com.Alacan.demo.Pedido.application.ObtenerPedidoPorId;

import org.springframework.http.ResponseEntity;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final CrearPedidoService crearPedidoService;
    private final AgregarProductoAlPedido agregarProductoAlPedido;
    private final AgregarComboAlPedido agregarComboAlPedido;
    private final ObtenerPedidoPorId obtenerPedidoPorId;

    public PedidoController(CrearPedidoService crearPedidoService,
            AgregarProductoAlPedido agregarProductoAlPedido,
            AgregarComboAlPedido agregarComboAlPedido,
            ObtenerPedidoPorId obtenerPedidoPorId) {
        this.crearPedidoService = crearPedidoService;
        this.agregarProductoAlPedido = agregarProductoAlPedido;
        this.agregarComboAlPedido = agregarComboAlPedido;
        this.obtenerPedidoPorId = obtenerPedidoPorId;
    }

    @PostMapping
    public ResponseEntity<Long> crearPedido(@RequestBody CrearPedidoRequest request) {

        Long id = crearPedidoService.ejecutar(request);

        return ResponseEntity.ok(id);
    }

    @PostMapping("/{pedidoId}/productos")
    public ResponseEntity<Void> agregarProducto(
            @PathVariable Long pedidoId,
            @RequestBody AgregarProductoRequest request) {

        agregarProductoAlPedido.ejecutar(
                pedidoId,
                request.getProductoId(),
                request.getCantidad(),
                request.getObservacion());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{pedidoId}/combos")
    public ResponseEntity<Void> agregarCombo(
            @PathVariable Long pedidoId,
            @RequestBody AgregarComboRequest request) {

        agregarComboAlPedido.ejecutar(
                pedidoId,
                request.getComboId(),
                request.getCantidad(),
                request.getObservacion());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{pedidoId}")
    public ResponseEntity<PedidoDTO> obtenerPedido(@PathVariable Long pedidoId) {

        PedidoDTO dto = obtenerPedidoPorId.ejecutar(pedidoId);

        return ResponseEntity.ok(dto);
    }

    /* 
    @PostMapping("/{pedidoId}/confirmar")
    public ResponseEntity<Void> confirmar(@PathVariable Long pedidoId) {

        confirmarPedidoService.ejecutar(pedidoId);

        return ResponseEntity.ok().build();
    }*/
}
