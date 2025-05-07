package com.example.ecommercezapatillas.services;


import com.example.ecommercezapatillas.dto.CategoriaMenuDTO;
import com.example.ecommercezapatillas.dto.SubcategoriaDTO;
import com.example.ecommercezapatillas.entities.Categoria;
import com.example.ecommercezapatillas.entities.enums.Sexo;
import com.example.ecommercezapatillas.repositories.CategoriaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService extends BaseService<Categoria, Long> {

    public CategoriaService(CategoriaRepository categoriaRepository) {
        super(categoriaRepository);
    }

    @Autowired
    private CategoriaRepository categoriaRepository;
    @Transactional
    public List<Categoria> listarSubcategorias(Long idPadre) {
        return categoriaRepository.findByCategoriaPadreId(idPadre);
    }

    @Transactional
    public List<Categoria> listarCategoriasRaiz() {
        return categoriaRepository.findByCategoriaPadreIsNull();
    }

    public List<CategoriaMenuDTO> obtenerMenuPorSexo(Sexo sexo) {
        List<Categoria> categoriasPadre = categoriaRepository.findByCategoriaPadreIsNull();
        List<CategoriaMenuDTO> resultado = new ArrayList<>();

        for (Categoria padre : categoriasPadre) {
            List<SubcategoriaDTO> subcategoriasConSexo = padre.getSubcategorias().stream()
                    .filter(sub -> sub.getProductos().stream().anyMatch(p -> p.getSexo() == sexo))
                    .map(sub -> new SubcategoriaDTO(sub.getId(), sub.getDenominacion()))
                    .collect(Collectors.toList());

            if (!subcategoriasConSexo.isEmpty()) {
                resultado.add(new CategoriaMenuDTO(padre.getDenominacion(), subcategoriasConSexo));
            }
        }

        return resultado;
    }

}
