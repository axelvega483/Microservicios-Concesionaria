package com.Concesionaria.payments_service.service;

import com.Concesionaria.payments_service.DTO.GenerarPagosRequestDTO;
import com.Concesionaria.payments_service.DTO.PagosGetDTO;
import com.Concesionaria.payments_service.DTO.PagosPutDTO;
import com.Concesionaria.payments_service.util.MetodoPago;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IPagosService {

    PagosGetDTO findById(Integer id);

    List<PagosGetDTO> findAll();

    PagosGetDTO update(Integer id, PagosPutDTO put);

    PagosGetDTO delete(Integer id);

    List<PagosGetDTO> generarPagos(GenerarPagosRequestDTO request);

    PagosGetDTO confirmarPago(Integer id, MetodoPago metodoPago);

    List<PagosGetDTO> findByVentaId(Integer ventaId);

    List<PagosGetDTO> getPagosPorVentas(List<Integer> ventaIds);

    BigDecimal getTotalPagado(Integer ventaId);

    Integer getCantidadPagos(Integer ventaId);

    Optional<PagosGetDTO> getUltimoPago(Integer ventaId);
}
