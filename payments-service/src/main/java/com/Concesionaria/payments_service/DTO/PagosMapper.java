package com.Concesionaria.payments_service.DTO;

import com.Concesionaria.payments_service.model.Pagos;
import org.springframework.stereotype.Component;

@Component
public class PagosMapper {

    public PagosGetDTO toDTO(Pagos pagos) {
        PagosGetDTO dto = new PagosGetDTO();
        dto.setActivo(pagos.getActivo());
        dto.setEstado(pagos.getEstado());
        dto.setFechaPago(pagos.getFechaPago());
        dto.setId(pagos.getId());
        dto.setMetodoPago(pagos.getMetodoPago());
        dto.setMonto(pagos.getMonto());
        dto.setVentaId(pagos.getVentaId());

        return dto;
    }

    public Pagos update(Pagos pagos, PagosPutDTO put) {
        if (put.getFechaPago() != null) {
            pagos.setFechaPago(put.getFechaPago());
        }

        if (put.getMetodoPago() != null) {
            pagos.setMetodoPago(put.getMetodoPago());
        }

        if (put.getEstado() != null) {
            pagos.setEstado(put.getEstado());
        }

        if (put.getActivo() != null) {
            pagos.setActivo(put.getActivo());
        }

        return pagos;
    }
}
