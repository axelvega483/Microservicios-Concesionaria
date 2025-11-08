package com.Concesionaria.sales_service.controller;

import com.Concesionaria.sales_service.DTO.*;
import com.Concesionaria.sales_service.service.IVentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityExistsException;
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
@Tag(name = "Ventas", description = "Controlador para operaciones de ventas")
public class VentaController {

    @Autowired
    private IVentaService ventaService;

    @Operation(summary = "Crear nueva venta", description = "Registra una nueva venta en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venta creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflicto - la entidad ya existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("")
    public ResponseEntity<VentaGetDTO> crear(
            @Parameter(description = "Datos de la venta a crear", required = true)
            @Valid @RequestBody VentaPostDTO postDTO) {
        VentaGetDTO dto = ventaService.create(postDTO);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todas las ventas", description = "Devuelve una lista con todas las ventas registradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ventas listadas correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<VentaGetDTO>> listarVenta() {
        List<VentaGetDTO> dto = ventaService.findAll();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Obtener venta por ID", description = "Devuelve una venta específica basada en su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("{id}")
    public ResponseEntity<VentaGetDTO> obtenerVenta(
            @Parameter(description = "ID de la venta a buscar", example = "1", required = true)
            @PathVariable Integer id) {
        VentaGetDTO venta = ventaService.findById(id);
        return new ResponseEntity<>(venta, HttpStatus.OK);
    }

    @Operation(summary = "Actualizar venta existente", description = "Actualiza la información de una venta existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflicto en la actualización"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("{id}")
    public ResponseEntity<VentaGetDTO> actualizar(
            @Parameter(description = "ID de la venta a actualizar", example = "1", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Datos actualizados de la venta", required = true)
            @RequestBody VentaPutDTO putDTO) {
        VentaGetDTO dto = ventaService.update(id, putDTO);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Eliminar venta", description = "Elimina una venta del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("{id}")
    public ResponseEntity<VentaGetDTO> eliminar(
            @Parameter(description = "ID de la venta a eliminar", example = "1", required = true)
            @PathVariable Integer id) {
        VentaGetDTO venta = ventaService.findById(id);
        ventaService.delete(venta.getId());
        return new ResponseEntity<>(venta, HttpStatus.OK);
    }

    @Operation(summary = "Obtener ventas por vehículo", description = "Devuelve las ventas asociadas a un vehículo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ventas encontradas exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<List<VehiculoVentaDetalleDTO>> obtenerVentasPorVehiculo(
            @Parameter(description = "ID del vehículo", example = "1", required = true)
            @PathVariable Integer vehiculoId) {
        List<VehiculoVentaDetalleDTO> ventas = ventaService.obtenerVentasPorVehiculo(vehiculoId);
        return new ResponseEntity<>(ventas, HttpStatus.OK);
    }

    @Operation(summary = "Obtener ventas por cliente", description = "Devuelve las ventas asociadas a un cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ventas encontradas exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ClienteVentaDTO>> obtenerVentasPorCliente(
            @Parameter(description = "ID del cliente", example = "1", required = true)
            @PathVariable Integer clienteId) {
        List<ClienteVentaDTO> ventas = ventaService.obtenerVentasPorCliente(clienteId);
        return new ResponseEntity<>(ventas, HttpStatus.OK);
    }

    @Operation(summary = "Obtener ventas por usuario", description = "Devuelve las ventas asociadas a un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ventas encontradas exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserVentaDTO>> obtenerVentasPorUser(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Integer userId) {
        List<UserVentaDTO> ventas = ventaService.obtenerVentasPorUser(userId);
        return new ResponseEntity<>(ventas, HttpStatus.OK);
    }

    @Operation(summary = "Actualizar saldo de venta", description = "Actualiza el saldo pendiente de una venta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/ventas/{ventaId}/saldo")
    public ResponseEntity<Void> actualizarSaldoVenta(
            @Parameter(description = "ID de la venta", example = "1", required = true)
            @PathVariable Integer ventaId,
            @Parameter(description = "Monto pagado", example = "15000.00", required = true)
            @RequestParam BigDecimal montoPagado) {
        VentaGetDTO venta = ventaService.findById(ventaId);
        ventaService.actualizarSaldo(ventaId, montoPagado);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}