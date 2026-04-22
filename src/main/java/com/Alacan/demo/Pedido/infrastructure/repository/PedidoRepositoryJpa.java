package com.Alacan.demo.Pedido.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Alacan.demo.Pedido.domain.model.Pedido;

@Repository
public interface PedidoRepositoryJpa extends JpaRepository<Pedido, Long> {
}
