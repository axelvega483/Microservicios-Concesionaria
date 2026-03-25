package com.Concesionaria.payments_service.DTO;

import com.Concesionaria.payments_service.model.Pagos;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PagosMapper {

    public PagosGetDTO toDTO(Pagos pagos) {
        return new PagosGetDTO(
                pagos.getId(),
                pagos.getFechaPago(),
                pagos.getMetodoPago(),
                pagos.getMonto(),
                pagos.getEstado(),
                pagos.isActivo(),
                pagos.getVentaId()
        );
    }

    public void fromUpdateDTO(Pagos pagos, PagosPutDTO put) {
        if (put.fechaPago() != null) {
            pagos.setFechaPago(put.fechaPago());
        }

        if (put.metodoPago() != null) {
            pagos.setMetodoPago(put.metodoPago());
        }

        if (put.estado() != null) {
            pagos.setEstado(put.estado());
        }
    }
    public List<PagosGetDTO> toDTOList(List<Pagos> pagos) {
        return pagos.stream().filter(Pagos::isActivo).map(this::toDTO).toList();
    }
}
