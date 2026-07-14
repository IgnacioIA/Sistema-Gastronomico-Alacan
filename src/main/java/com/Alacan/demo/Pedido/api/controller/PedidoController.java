package com.Alacan.demo.Pedido.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Alacan.demo.Pedido.api.dto.AgregarComboRequest;
import com.Alacan.demo.Pedido.api.dto.AgregarProductoRequest;
import com.Alacan.demo.Pedido.api.dto.CrearPedidoRequest;
import com.Alacan.demo.Pedido.api.dto.ModificarCantidadRequest;
import com.Alacan.demo.Pedido.api.dto.PedidoDTO;
import com.Alacan.demo.Pedido.application.AgregarComboAlPedido;
import com.Alacan.demo.Pedido.application.AgregarProductoAlPedido;
import com.Alacan.demo.Pedido.application.CancelarPedidoService;
import com.Alacan.demo.Pedido.application.ConfirmarPedidoService;
import com.Alacan.demo.Pedido.application.CrearPedidoService;
import com.Alacan.demo.Pedido.application.EliminarItemService;
import com.Alacan.demo.Pedido.application.ListarPedidosService;
import com.Alacan.demo.Pedido.application.MarcarComoEntregadoService;
import com.Alacan.demo.Pedido.application.MarcarComoListoService;
import com.Alacan.demo.Pedido.application.ModificarCantidad;
import com.Alacan.demo.Pedido.application.ObtenerPedidoPorId;
import com.Alacan.demo.Pedido.application.PasarAEnPreparacionService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final CrearPedidoService crearPedidoService;
    private final AgregarProductoAlPedido agregarProductoAlPedido;
    private final AgregarComboAlPedido agregarComboAlPedido;
    private final EliminarItemService eliminarItemService;
    private final ModificarCantidad modificarCantidad;
    private final ObtenerPedidoPorId obtenerPedidoPorId;
    private final ListarPedidosService listarPedidosService;
    private final ConfirmarPedidoService confirmarPedidoService;
    private final CancelarPedidoService cancelarPedidoService;
    private final PasarAEnPreparacionService pasarAEnPreparacionService;
    private final MarcarComoListoService marcarComoListoService;
    private final MarcarComoEntregadoService marcarComoEntregadoService;

    public PedidoController(
            CrearPedidoService crearPedidoService,
            AgregarProductoAlPedido agregarProductoAlPedido,
            AgregarComboAlPedido agregarComboAlPedido,
            EliminarItemService eliminarItemService,
            ModificarCantidad modificarCantidad,
            ObtenerPedidoPorId obtenerPedidoPorId,
            ListarPedidosService listarPedidosService,
            ConfirmarPedidoService confirmarPedidoService,
            CancelarPedidoService cancelarPedidoService,
            PasarAEnPreparacionService pasarAEnPreparacionService,
            MarcarComoListoService marcarComoListoService,
            MarcarComoEntregadoService marcarComoEntregadoService) {
        this.crearPedidoService = crearPedidoService;
        this.agregarProductoAlPedido = agregarProductoAlPedido;
        this.agregarComboAlPedido = agregarComboAlPedido;
        this.eliminarItemService = eliminarItemService;
        this.modificarCantidad = modificarCantidad;
        this.obtenerPedidoPorId = obtenerPedidoPorId;
        this.listarPedidosService = listarPedidosService;
        this.confirmarPedidoService = confirmarPedidoService;
        this.cancelarPedidoService = cancelarPedidoService;
        this.pasarAEnPreparacionService = pasarAEnPreparacionService;
        this.marcarComoListoService = marcarComoListoService;
        this.marcarComoEntregadoService = marcarComoEntregadoService;
    }

    // --- Consultas ---

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarPedidos() {
        return ResponseEntity.ok(listarPedidosService.ejecutar());
    }

    @GetMapping("/{pedidoId}")
    public ResponseEntity<PedidoDTO> obtenerPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(obtenerPedidoPorId.ejecutar(pedidoId));
    }

    // --- Gestión del pedido ---

    @PostMapping
    public ResponseEntity<Long> crearPedido(@RequestBody CrearPedidoRequest request) {
        Long id = crearPedidoService.ejecutar(request);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/{pedidoId}/productos")
    public ResponseEntity<Void> agregarProducto(
            @PathVariable Long pedidoId,
            @RequestBody AgregarProductoRequest request) {
        agregarProductoAlPedido.ejecutar(pedidoId, request.getProductoId(), request.getCantidad(), request.getObservacion());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{pedidoId}/combos")
    public ResponseEntity<Void> agregarCombo(
            @PathVariable Long pedidoId,
            @RequestBody AgregarComboRequest request) {
        agregarComboAlPedido.ejecutar(pedidoId, request.getComboId(), request.getCantidad(), request.getObservacion());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{pedidoId}/items/{itemId}")
    public ResponseEntity<Void> eliminarItem(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId) {
        eliminarItemService.ejecutar(pedidoId, itemId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{pedidoId}/items/{itemId}/cantidad")
    public ResponseEntity<Void> modificarCantidad(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId,
            @RequestBody ModificarCantidadRequest request) {
        modificarCantidad.ejecutar(pedidoId, itemId, request.getCantidad());
        return ResponseEntity.ok().build();
    }

    // --- Transiciones de estado ---

    @PostMapping("/{pedidoId}/confirmar")
    public ResponseEntity<Void> confirmar(@PathVariable Long pedidoId) {
        confirmarPedidoService.ejecutar(pedidoId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{pedidoId}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long pedidoId) {
        cancelarPedidoService.ejecutar(pedidoId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{pedidoId}/en-preparacion")
    public ResponseEntity<Void> pasarAEnPreparacion(@PathVariable Long pedidoId) {
        pasarAEnPreparacionService.ejecutar(pedidoId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{pedidoId}/listo")
    public ResponseEntity<Void> marcarComoListo(@PathVariable Long pedidoId) {
        marcarComoListoService.ejecutar(pedidoId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{pedidoId}/entregado")
    public ResponseEntity<Void> marcarComoEntregado(@PathVariable Long pedidoId) {
        marcarComoEntregadoService.ejecutar(pedidoId);
        return ResponseEntity.ok().build();
    }
}
