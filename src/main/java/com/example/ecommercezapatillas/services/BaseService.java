package com.example.ecommercezapatillas.services;


import com.example.ecommercezapatillas.entities.Base;
import com.example.ecommercezapatillas.repositories.BaseRepository;
import org.springframework.transaction.annotation.Transactional;


import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class BaseService<E extends Base, ID extends Serializable> {

    protected BaseRepository<E, ID> baseRepository;

    public BaseService(BaseRepository<E, ID> baseRepository){
        this.baseRepository = baseRepository;
    }
    @Transactional

    public List<E> listar() throws Exception {
        try {
            return baseRepository.findAll();
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public Optional<E> buscarPorId(ID id) throws Exception {
        try {
            return Optional.ofNullable(baseRepository.findById(id).orElse(null));
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public E crear(E entity) throws Exception {
        try{
            return baseRepository.save(entity);
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public E actualizar(E entity) throws Exception {
        try{
            return baseRepository.save(entity);
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }
    public void eliminar(ID id) throws Exception {
        try {
            E entidad = baseRepository.findById(id)
                    .orElseThrow(() -> new Exception("Entidad no encontrada con id: " + id));
            entidad.setActive(false);
            baseRepository.save(entidad);
        } catch (Exception e) {
            throw new Exception("Error al hacer borrado lógico: " + e.getMessage());
        }
    }

}
