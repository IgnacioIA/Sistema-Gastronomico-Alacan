package com.Alacan.demo.Catalogo.infraestructure;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.Alacan.demo.Catalogo.domain.model.Combo;
import com.Alacan.demo.Catalogo.domain.repository.ComboRepository;

@Repository
public class ComboRepositoryImp implements ComboRepository{

    private ComboRepositoryJpa comboRepositoryJpa;

    public ComboRepositoryImp(ComboRepositoryJpa comboRepositoryJpa){
        this.comboRepositoryJpa = comboRepositoryJpa;
    }

    @Override
    public Optional<Combo> buscarPorId(Long id) {
        return comboRepositoryJpa.findById(id);
    }

    @Override
    public List<Combo> buscarTodos() {
        return comboRepositoryJpa.findAll();
    }

    @Override
    public void guardar(Combo combo) {
        comboRepositoryJpa.save(combo);
    }

   
    
}
