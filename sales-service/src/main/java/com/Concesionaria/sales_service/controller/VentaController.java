package com.Concesionaria.sales_service.controller;

import com.Concesionaria.sales_service.DTO.*;
import com.Concesionaria.sales_service.service.IVentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("sales")
public class VentaController {

    @Autowired
    private IVentaService ventaService;


    @PostMapping("")
    public ResponseEntity<?> crear(@Valid @RequestBody VentaPostDTO postDTO) {
        VentaGetDTO dto = ventaService.create(postDTO);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<?> listarVenta() {
        List<VentaGetDTO> dto = ventaService.findAll();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @GetMapping("{id}")
    public ResponseEntity<?> obtenerVenta(@PathVariable Integer id) {
        VentaGetDTO venta = ventaService.findById(id);
        return new ResponseEntity<>(venta, HttpStatus.OK);
    }


    @PutMapping("{id}/anular")
    public ResponseEntity<?> anular(@PathVariable Integer id) {
        VentaGetDTO dto = ventaService.anular(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @DeleteMapping("{id}")
    public ResponseEntity<VentaGetDTO> eliminar(@PathVariable Integer id) {
        VentaGetDTO venta = ventaService.findById(id);
        ventaService.delete(venta.id());
        return new ResponseEntity<>(venta, HttpStatus.OK);
    }


    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<List<VehiculoVentaDetalleDTO>> obtenerVentasPorVehiculo(@PathVariable Integer vehiculoId) {
        List<VehiculoVentaDetalleDTO> ventas = ventaService.obtenerVentasPorVehiculo(vehiculoId);
        return new ResponseEntity<>(ventas, HttpStatus.OK);
    }


    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ClienteVentaDTO>> obtenerVentasPorCliente(@PathVariable Integer clienteId) {
        List<ClienteVentaDTO> ventas = ventaService.obtenerVentasPorCliente(clienteId);
        return new ResponseEntity<>(ventas, HttpStatus.OK);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserVentaDTO>> obtenerVentasPorUser(@PathVariable Integer userId) {
        List<UserVentaDTO> ventas = ventaService.obtenerVentasPorUser(userId);
        return new ResponseEntity<>(ventas, HttpStatus.OK);
    }


    @PutMapping("/ventas/{ventaId}/saldo")
    public ResponseEntity<Void> actualizarSaldoVenta(@PathVariable Integer ventaId, @RequestParam BigDecimal montoPagado) {
        VentaGetDTO venta = ventaService.findById(ventaId);
        ventaService.actualizarSaldo(ventaId, montoPagado);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}