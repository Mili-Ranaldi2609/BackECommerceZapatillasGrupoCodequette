package com.example.ecommercezapatillas.services;

import com.example.ecommercezapatillas.entities.Descuentos;
import com.example.ecommercezapatillas.repositories.DescuentosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DescuentosService extends BaseService<Descuentos, Long> {

    private final DescuentosRepository descuentosRepository;

    public DescuentosService(DescuentosRepository descuentosRepository) {
        super(descuentosRepository);
        this.descuentosRepository = descuentosRepository;
    }

    @Override
    public Descuentos crear(Descuentos descuento) throws Exception {
        validarDescuento(descuento);
        try {
            return super.crear(descuento);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    @Override
    public Descuentos actualizar(Descuentos descuento) throws Exception {
        validarDescuento(descuento);
        try {
            return super.actualizar(descuento);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    public List<Descuentos> obtenerPorIdDescuento(Long idDescuento) throws Exception {
        try {
            return descuentosRepository.findAllByDescuentoId(idDescuento);
        } catch (Exception e) {
            throw new Exception("No se pudieron obtener los descuentos por ID.");
        }
    }

    public List<Descuentos> obtenerPorIdArticulo(Long idProducto) throws Exception {
        try {
            return descuentosRepository.findAllByArticuloId(idProducto);
        } catch (Exception e) {
            throw new Exception("No se pudieron obtener los descuentos por producto.");
        }
    }

    private void validarDescuento(Descuentos descuento) throws Exception {
        try {
            if (descuento.getFechaDesde() == null || descuento.getFechaHasta() == null ||
                    descuento.getFechaDesde().isAfter(descuento.getFechaHasta())) {
                throw new Exception("Las fechas del descuento son inválidas.");
            }

            if (descuento.getHoraDesde() == null || descuento.getHoraHasta() == null ||
                    !descuento.getHoraDesde().isBefore(descuento.getHoraHasta())) {
                throw new Exception("Las horas del descuento son inválidas.");
            }

            if (descuento.getPrecioPromocional() == null || descuento.getPrecioPromocional() <= 0) {
                throw new Exception("El precio promocional debe ser mayor a 0.");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        if (descuento.getDenominacion() == null || descuento.getDenominacion().isEmpty()) {
            throw new Exception("La denominación no puede estar vacía.");
        }


    }
}

