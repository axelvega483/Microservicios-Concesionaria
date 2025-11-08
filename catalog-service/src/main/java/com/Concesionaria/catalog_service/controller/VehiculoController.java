package com.Concesionaria.catalog_service.controller;

import com.Concesionaria.catalog_service.DTO.VehiculoGetDTO;
import com.Concesionaria.catalog_service.DTO.VehiculoPostDTO;
import com.Concesionaria.catalog_service.DTO.VehiculoPutDTO;
import com.Concesionaria.catalog_service.service.IServiceImagen;
import com.Concesionaria.catalog_service.service.IVehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@Tag(name = "Vehículos", description = "Controlador para operaciones del catálogo de vehículos")
public class VehiculoController {

    @Autowired
    private IVehiculoService vehiculoService;

    @Autowired
    private IServiceImagen imagenservice;

    @Value("${app.ruta.imagenes}")
    private String rutaImagenes;

    @Operation(summary = "Crear nuevo vehículo", description = "Registra un nuevo vehículo en el catálogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vehículo creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflicto - el vehículo ya existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("crear")
    public ResponseEntity<VehiculoGetDTO> crear(
            @Parameter(description = "Datos del vehículo a crear", required = true)
            @Valid @RequestBody VehiculoPostDTO postDTO) {
        VehiculoGetDTO dto = vehiculoService.createVehiculo(postDTO);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todos los vehículos", description = "Devuelve una lista con todos los vehículos del catálogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículos listados correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<VehiculoGetDTO>> listarVehiculos() {
        List<VehiculoGetDTO> dto = vehiculoService.findAllVehiculo();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Obtener vehículo por ID", description = "Devuelve un vehículo específico basado en su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículo encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("{id}")
    public ResponseEntity<VehiculoGetDTO> obtenerVehiculo(
            @Parameter(description = "ID del vehículo a buscar", example = "1", required = true)
            @PathVariable Integer id) {
        VehiculoGetDTO vehiculo = vehiculoService.findByIdVehiculo(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro vehiculo"));
        return new ResponseEntity<>(vehiculo, HttpStatus.OK);
    }

    @Operation(summary = "Actualizar vehículo existente", description = "Actualiza la información de un vehículo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículo actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto en la actualización"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("{id}")
    public ResponseEntity<VehiculoGetDTO> actualizar(
            @Parameter(description = "ID del vehículo a actualizar", example = "1", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Datos actualizados del vehículo", required = true)
            @RequestBody VehiculoPutDTO putDTO) {
        VehiculoGetDTO dto = vehiculoService.updateVehiculo(id, putDTO);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Eliminar vehículo", description = "Elimina un vehículo del catálogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículo eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("{id}")
    public ResponseEntity<VehiculoGetDTO> eliminar(
            @Parameter(description = "ID del vehículo a eliminar", example = "1", required = true)
            @PathVariable Integer id) {
        VehiculoGetDTO vehiculo = vehiculoService.findByIdVehiculo(id)
                .orElseThrow(() -> new EntityNotFoundException("vehiculo no encontrado"));
        vehiculoService.deleteByIdVehiculo(vehiculo.getId());
        return new ResponseEntity<>(vehiculo, HttpStatus.OK);
    }

    @Operation(summary = "Subir imágenes para vehículo", description = "Sube una o múltiples imágenes para un vehículo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Imágenes subidas exitosamente"),
            @ApiResponse(responseCode = "400", description = "Archivos inválidos o formato no soportado"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al subir imágenes")
    })
    @PostMapping(value = "/{id}/imagenes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VehiculoGetDTO> subirImagenesVehiculo(
            @Parameter(description = "ID del vehículo", example = "1", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Archivos de imagen a subir", required = true)
            @RequestParam("imagenes") MultipartFile[] imagenes) {
        VehiculoGetDTO dto = vehiculoService.subirImagenesVehiculo(id, imagenes);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener imagen de vehículo", description = "Devuelve una imagen específica de un vehículo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Imagen o vehículo no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{vehiculoId}/imagen/{imagenId}")
    public ResponseEntity<Resource> verImagen(
            @Parameter(description = "ID del vehículo", example = "1", required = true)
            @PathVariable Integer vehiculoId,
            @Parameter(description = "ID de la imagen", example = "1", required = true)
            @PathVariable Integer imagenId) {
        return vehiculoService.obtenerImagen(vehiculoId, imagenId);
    }

    @Operation(summary = "Eliminar imagen de vehículo", description = "Elimina una imagen específica de un vehículo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Imagen o vehículo no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{vehiculoId}/imagen/{imagenId}")
    public ResponseEntity<VehiculoGetDTO> eliminarImagenVehiculo(
            @Parameter(description = "ID del vehículo", example = "1", required = true)
            @PathVariable Integer vehiculoId,
            @Parameter(description = "ID de la imagen a eliminar", example = "1", required = true)
            @PathVariable Integer imagenId) {
        VehiculoGetDTO dto = vehiculoService.eliminarImagenVehiculo(vehiculoId, imagenId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}