package com.Concesionaria.sales_service.service;

import com.Concesionaria.sales_service.DTO.*;

import java.util.List;

public interface IVentaService {

    VentaGetDTO create(VentaPostDTO post);

    VentaGetDTO update(Integer id, VentaPutDTO put);

    VentaGetDTO delete(Integer id);

    VentaGetDTO findById(Integer id);

    List<VentaGetDTO> findAll();

    List<VehiculoVentaDetalleDTO> obtenerVentasPorVehiculo(Integer vehiculoId);

    List<UserVentaDTO> obtenerVentasPorUser(Integer userId);

    List<ClienteVentaDTO> obtenerVentasPorCliente(Integer clienteId);

}
