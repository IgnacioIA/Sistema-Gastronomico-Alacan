package com.Alacan.demo.Pedido.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    @Enumerated(EnumType.STRING)
    private TipoPedido tipoPedido;

    @Enumerated(EnumType.STRING)
    private CanalPedido canal;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pedido_id")
    private List<ItemPedido> items = new ArrayList<>();

    private Long clienteId;

    private String direccion;

    private LocalDateTime fechaCreacion;

    private BigDecimal total;

    protected Pedido() {
    } // requerido por JPA

    public Pedido(TipoPedido tipoPedido, CanalPedido canal, String direccion, Long clienteId) {
        this.estado = EstadoPedido.NUEVO;
        this.tipoPedido = tipoPedido;
        this.canal = canal;
        this.direccion = direccion;
        this.clienteId = clienteId;
        this.fechaCreacion = LocalDateTime.now();
        this.total = BigDecimal.ZERO;

        validarDireccion();
    }

    private void validarDireccion() {
        if (tipoPedido == TipoPedido.DELIVERY && (direccion == null || direccion.isBlank())) {
            throw new IllegalStateException("Un pedido delivery requiere dirección");
        }
    }

    public void agregarItem(Long productoId, String nombre, BigDecimal precio, int cantidad, String observacion) {

        validarAgregarItem();

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        Optional<ItemPedido> existente = buscarItem(productoId);

        if (existente.isPresent()) {
            existente.get().aumentarCantidad(cantidad);
        } else {
            items.add(new ItemPedido(productoId, nombre, precio, cantidad, observacion));
        }

        recalcularTotal();
    }

    public void eliminarItem(Long productoId) {

        validarEliminarItem();

        ItemPedido item = buscarItem(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado"));

        items.remove(item);

        recalcularTotal();
    }

    public void modificarCantidadItem(Long itemId, int nuevaCantidad) {

        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        validarModificarCantidad();

        ItemPedido item = buscarItem(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado"));

        if (item == null) {
            throw new IllegalArgumentException("El item no existe en el pedido");
        }

        if (nuevaCantidad == 0) {
            items.remove(item);
        } else {
            item.modificarCantidad(nuevaCantidad);
        }

        recalcularTotal();
    }

    public void disminuirCantidadItem(Long productoId, int cantidad) {

        validarModificarCantidad();

        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        ItemPedido item = buscarItem(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado"));

        if (item.quedariaEnCero(cantidad)) {
            items.remove(item);
        } else {
            item.disminuirCantidad(cantidad);
        }

        recalcularTotal();
    }

    private Optional<ItemPedido> buscarItem(Long productoId) {
        return items.stream()
                .filter(i -> i.esDelProducto(productoId))
                .findFirst();
    }

    private void validarAgregarItem() {
        if (!estado.permiteAgregarItem()) {
            throw new IllegalStateException("No se pueden agregar items en este estado");
        }
    }

    private void validarEliminarItem() {
        if (!estado.permiteEliminarItem()) {
            throw new IllegalStateException("No se pueden eliminar items en este estado");
        }
    }

    private void validarModificarCantidad() {
        if (!estado.permiteModificarCantidad()) {
            throw new IllegalStateException("No se puede modificar la cantidad en este estado");
        }
    }

    private void recalcularTotal() {
        this.total = items.stream()
                .map(ItemPedido::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Cambios de estados
    public void confirmar() {
        if (estado != EstadoPedido.NUEVO) {
            throw new IllegalStateException("Solo pedidos nuevos pueden confirmarse");
        }

        if (items.isEmpty()) {
            throw new IllegalStateException("No se puede confirmar un pedido sin items");
        }

        this.estado = EstadoPedido.CONFIRMADO;
    }

    public void pasarAEnPreparacion() {
        if (estado != EstadoPedido.CONFIRMADO) {
            throw new IllegalStateException("Solo pedidos confirmados pasan a preparación");
        }

        this.estado = EstadoPedido.EN_PREPARACION;
    }

    public void marcarComoListo() {
        if (estado != EstadoPedido.EN_PREPARACION) {
            throw new IllegalStateException("El pedido debe estar en preparación");
        }

        this.estado = EstadoPedido.LISTO;
    }

    public void marcarComoEnviado() {
        if (tipoPedido != TipoPedido.DELIVERY) {
            throw new IllegalStateException("Solo pedidos delivery pueden enviarse");
        }

        if (estado != EstadoPedido.LISTO) {
            throw new IllegalStateException("Debe estar listo para enviarse");
        }

        this.estado = EstadoPedido.ENVIADO;
    }

    public void marcarComoEntregado() {

        if (estado != EstadoPedido.LISTO && estado != EstadoPedido.ENVIADO) {
            throw new IllegalStateException("Estado inválido para entregar");
        }

        this.estado = EstadoPedido.ENTREGADO;
    }

    public void cancelar() {

        if (estado == EstadoPedido.ENTREGADO) {
            throw new IllegalStateException("No se puede cancelar un pedido entregado");
        }

        this.estado = EstadoPedido.CANCELADO;
    }

    public Long getId() {
        return id;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public List<ItemPedido> getItems() {
        return Collections.unmodifiableList(items);
    }
}
