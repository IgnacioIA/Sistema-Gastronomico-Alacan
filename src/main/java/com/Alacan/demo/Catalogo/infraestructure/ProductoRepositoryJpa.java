package com.Alacan.demo.Catalogo.infraestructure;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.Alacan.demo.Catalogo.domain.model.Producto;

@Repository
public interface ProductoRepositoryJpa extends JpaRepository<Producto, Long>  {
    
}
