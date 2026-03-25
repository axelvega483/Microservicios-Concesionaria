package com.Concesionaria.catalog_service.controller;

import com.Concesionaria.catalog_service.DTO.VehiculoGetDTO;
import com.Concesionaria.catalog_service.DTO.VehiculoPostDTO;
import com.Concesionaria.catalog_service.DTO.VehiculoPutDTO;
import com.Concesionaria.catalog_service.service.IVehiculoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("catalog")
public class VehiculoController {

    @Autowired
    private IVehiculoService vehiculoService;

    @PostMapping("crear")
    public ResponseEntity<?> crear(@Valid @RequestBody VehiculoPostDTO postDTO) {
        VehiculoGetDTO dto = vehiculoService.createVehiculo(postDTO);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<?> listarVehiculos() {
        List<VehiculoGetDTO> dto = vehiculoService.findAllVehiculo();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @GetMapping("{id}")
    public ResponseEntity<?> obtenerVehiculo(@PathVariable Integer id) {
        VehiculoGetDTO vehiculo = vehiculoService.findByIdVehiculo(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro vehiculo"));
        return new ResponseEntity<>(vehiculo, HttpStatus.OK);
    }


    @PutMapping("{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody VehiculoPutDTO putDTO) {
        VehiculoGetDTO dto = vehiculoService.updateVehiculo(id, putDTO);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @DeleteMapping("{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        VehiculoGetDTO vehiculo = vehiculoService.findByIdVehiculo(id)
                .orElseThrow(() -> new EntityNotFoundException("vehiculo no encontrado"));
        vehiculoService.deleteByIdVehiculo(vehiculo.id());
        return new ResponseEntity<>(vehiculo, HttpStatus.OK);
    }


    @PostMapping(value = "/{id}/imagenes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirImagenesVehiculo(@PathVariable Integer id, @RequestParam("imagenes") MultipartFile[] imagenes) {
        VehiculoGetDTO dto = vehiculoService.subirImagenesVehiculo(id, imagenes);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }


    @GetMapping("/{vehiculoId}/imagen/{imagenId}")
    public ResponseEntity<Resource> verImagen(@PathVariable Integer vehiculoId, @PathVariable Integer imagenId) {
        return vehiculoService.obtenerImagen(vehiculoId, imagenId);
    }


    @DeleteMapping("/{vehiculoId}/imagen/{imagenId}")
    public ResponseEntity<?> eliminarImagenVehiculo(@PathVariable Integer vehiculoId, @PathVariable Integer imagenId) {
        VehiculoGetDTO dto = vehiculoService.eliminarImagenVehiculo(vehiculoId, imagenId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PutMapping("/{vehiculoId}/incrementar-stock")
    public ResponseEntity<?> incrementarStock(@PathVariable Integer vehiculoId, @RequestParam Integer cantidad) {
        VehiculoGetDTO vehiculo = vehiculoService.incrementarStock(vehiculoId, cantidad);
        return new ResponseEntity<>(vehiculo, HttpStatus.OK);
    }
    @PutMapping("/{vehiculoId}/descontar-stock")
    public ResponseEntity<?> descontarStock(@PathVariable Integer vehiculoId, @RequestParam Integer cantidad) {
        VehiculoGetDTO vehiculo = vehiculoService.descontarStock(vehiculoId, cantidad);
        return new ResponseEntity<>(vehiculo, HttpStatus.OK);
    }

    @GetMapping("/{vehiculoId}/venta")
    public ResponseEntity<?> getVehiculoById(@PathVariable Integer vehiculoId) {
        VehiculoGetDTO vehiculo = vehiculoService.vehiculoId(vehiculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado"));
        return new ResponseEntity<>(vehiculo, HttpStatus.OK);

    }
}