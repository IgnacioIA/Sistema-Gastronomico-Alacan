package com.Alacan.demo.Pedido.infrastructure.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.Alacan.demo.Pedido.domain.Pedido;
import com.Alacan.demo.Pedido.domain.PedidoRepository;

@Repository
public class PedidoRepositoryImpl implements PedidoRepository {

    private final PedidoRepositoryJpa jpaRepository;
    private List<Pedido> pedidos = new ArrayList<>();

    public PedidoRepositoryImpl(PedidoRepositoryJpa jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Pedido> buscarTodos() {
        return new ArrayList<>(pedidos);
    }

    @Override
    public Pedido guardar(Pedido pedido) {
        return jpaRepository.save(pedido);
    }

    @Override
    public Optional<Pedido> buscarPorId(Long id) {
        return jpaRepository.findById(id);
    }
}
