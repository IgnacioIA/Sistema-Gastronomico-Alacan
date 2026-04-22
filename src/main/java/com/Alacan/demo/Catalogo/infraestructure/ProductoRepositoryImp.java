package com.Alacan.demo.Catalogo.infraestructure;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.Alacan.demo.Catalogo.domain.model.Producto;
import com.Alacan.demo.Catalogo.domain.repository.ProductoRepository;

@Repository
public class ProductoRepositoryImp implements ProductoRepository {

    private ProductoRepositoryJpa productoRepositoryJpa;

    public ProductoRepositoryImp(ProductoRepositoryJpa productoRepositoryJpa) {
        this.productoRepositoryJpa = productoRepositoryJpa;
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        return productoRepositoryJpa.findById(id);
    }

    @Override
    public List<Producto> buscarTodos() {
        return productoRepositoryJpa.findAll();
    }

    @Override
    public void guardar(Producto producto) {
        productoRepositoryJpa.save(producto);
    }

}
