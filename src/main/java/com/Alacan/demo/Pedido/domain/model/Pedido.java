package com.Alacan.demo.Pedido.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.Alacan.demo.Catalogo.domain.model.Combo;
import com.Alacan.demo.Catalogo.domain.model.Producto;

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

    public void agregarProducto(Producto producto, int cantidad, String observacion) {
        if (producto == null) {
            throw new IllegalArgumentException("Producto requerido");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        ItemProducto existente = buscarItemProducto(producto.getId());

        if (existente != null) {
            existente.aumentarCantidad(cantidad);
        } else {
            items.add(new ItemProducto(
                    producto.getId(),
                    producto.getNombreProducto(),
                    producto.getPrecioVenta(),
                    cantidad,
                    observacion));
        }

        recalcularTotal();
    }

    public void agregarCombo(Combo combo, int cantidad, String observacion) {

        validarAgregarItem();

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        ItemCombo existente = buscarItemComboPorId(combo.getId());
        if (existente != null) {
            existente.aumentarCantidad(cantidad);
        } else {
            items.add(new ItemCombo(
                    combo.getId(),
                    combo.getNombreCombo(), // asumido
                    combo.getPrecioCombo(), // asumido
                    cantidad,
                    observacion));
        }

        recalcularTotal();
    }

    public void eliminarItem(Long itemId) {

        validarEliminarItem();

        ItemPedido item = buscarItemPorId(itemId);

        items.remove(item);

        recalcularTotal();
    }

    public void modificarCantidadItem(Long itemId, int nuevaCantidad) {

        validarModificarCantidad();

        ItemPedido item = buscarItemPorId(itemId);

        if (nuevaCantidad < 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        if (nuevaCantidad == 0) {
            items.remove(item);
        } else {
            item.modificarCantidad(nuevaCantidad);
        }

        recalcularTotal();
    }

    public void disminuirCantidadItem(Long itemId, int cantidad) {

        validarModificarCantidad();

        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        ItemPedido item = buscarItemPorId(itemId);

        if (item.getCantidad() == cantidad) {
            items.remove(item);
        } else {
            item.disminuirCantidad(cantidad);
        }

        recalcularTotal();
    }

    private ItemProducto buscarItemProducto(Long productoId) {
        return items.stream()
                .filter(i -> i instanceof ItemProducto)
                .map(i -> (ItemProducto) i)
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst()
                .orElse(null);
    }

    private ItemPedido buscarItemPorId(Long itemId) {
        return items.stream()
                .filter(i -> i.getId() != null && i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado"));
    }

    private ItemCombo buscarItemComboPorId(Long idItemCombo) {
        return items.stream()
                .filter(i -> i instanceof ItemCombo)
                .map(i -> (ItemCombo) i)
                .filter(i -> i.getComboId().equals(idItemCombo))
                .findFirst()
                .orElse(null);
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
