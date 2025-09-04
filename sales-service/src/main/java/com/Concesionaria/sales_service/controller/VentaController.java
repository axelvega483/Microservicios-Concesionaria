package com.Concesionaria.sales_service.controller;

import com.Concesionaria.sales_service.DTO.*;
import com.Concesionaria.sales_service.service.IVentaService;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("sales")
public class VentaController {
    @Autowired
    private IVentaService ventaService;

    @PostMapping("")
    public ResponseEntity<?> crear(@Valid @RequestBody VentaPostDTO postDTO) {
        try {
            VentaGetDTO dto = ventaService.create(postDTO);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (EntityExistsException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping
    public ResponseEntity<?> listarVenta() {
        try {
            List<VentaGetDTO> dto = ventaService.findAll();
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: "+e.getMessage(),  HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> obtenerVenta(@PathVariable Integer id) {
        try {
            VentaGetDTO venta = ventaService.findById(id);
            if (venta != null) {
                return new ResponseEntity<>(venta, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Venta no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error: ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody VentaPutDTO putDTO) {
        try {
            VentaGetDTO dto = ventaService.update(id, putDTO);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (EntityExistsException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            VentaGetDTO venta = ventaService.findById(id);
            if (venta != null) {
                ventaService.delete(venta.getId());
                return new ResponseEntity<>(venta, HttpStatus.OK);
            }
            return new ResponseEntity<>("cliente no encontrado: ",HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<?> obtenerVentasPorVehiculo(@PathVariable Integer vehiculoId) {
        try {
            List<VehiculoVentaDetalleDTO> ventas = ventaService.obtenerVentasPorVehiculo(vehiculoId);
            return new ResponseEntity<>(ventas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> obtenerVentasPorCliente(@PathVariable Integer clienteId) {
        try {
            List<ClienteVentaDTO> ventas = ventaService.obtenerVentasPorCliente(clienteId);
            return new ResponseEntity<>(ventas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> obtenerVentasPorUser(@PathVariable Integer userId) {
        try {
            List<UserVentaDTO> ventas = ventaService.obtenerVentasPorUser(userId);
            return new ResponseEntity<>(ventas,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
