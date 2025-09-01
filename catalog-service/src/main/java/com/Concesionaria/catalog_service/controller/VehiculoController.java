package com.Concesionaria.catalog_service.controller;

import com.Concesionaria.catalog_service.DTO.VehiculoGetDTO;
import com.Concesionaria.catalog_service.DTO.VehiculoPostDTO;
import com.Concesionaria.catalog_service.DTO.VehiculoPutDTO;
import com.Concesionaria.catalog_service.service.IServiceImagen;
import com.Concesionaria.catalog_service.service.IVehiculoService;
import com.Concesionaria.catalog_service.util.ApiResponse;
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
@RequestMapping("vehiculo")
public class VehiculoController {
    @Autowired
    private IVehiculoService vehiculoService;
    @Autowired
    private IServiceImagen imagenservice;


    @Value("${app.ruta.imagenes}")
    private String rutaImagenes;

    @PostMapping("crear")
    public ResponseEntity<?> crear(@Valid @RequestBody VehiculoPostDTO postDTO) {
        try {
            VehiculoGetDTO dto = vehiculoService.createVehiculo(postDTO);
            return new ResponseEntity<>(new ApiResponse<>("Vehiculo Creado", dto, true), HttpStatus.CREATED);
        } catch (EntityExistsException e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: " + e.getMessage(), null, false), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: " + e.getMessage(), null, false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> listarVehiculos() {
        try {
            List<VehiculoGetDTO> dto = vehiculoService.findAllVehiculo();

            return new ResponseEntity<>(new ApiResponse<>("Listado de vehiculos ", dto, true), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: " + e.getMessage(), null, false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> obtenerVehiculo(@PathVariable Integer id) {
        try {
            VehiculoGetDTO vehiculo = vehiculoService.findByIdVehiculo(id).orElse(null);
            if (vehiculo != null) {
                return new ResponseEntity<>(new ApiResponse<>("Vehiculo encontrado ", vehiculo, true), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse<>("No se encontro vehiculo", null, false), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: " + e.getMessage(), null, false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody VehiculoPutDTO putDTO) {
        try {
            VehiculoGetDTO dto = vehiculoService.updateVehiculo(id, putDTO);
            return new ResponseEntity<>(new ApiResponse<>("Vehiculo actualiazo", dto, true), HttpStatus.OK);
        } catch (EntityExistsException e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: " + e.getMessage(), null, false), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: " + e.getMessage(), null, false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            VehiculoGetDTO vehiculo = vehiculoService.findByIdVehiculo(id).orElse(null);
            if (vehiculo != null) {
                vehiculoService.deleteByIdVehiculo(vehiculo.getId());
                return new ResponseEntity<>(new ApiResponse<>("vehiculo dado de baja", vehiculo, true), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ApiResponse<>("vehiculo no encontrado: ", null, false), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: " + e.getMessage(), null, false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/{id}/imagenes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirImagenesVehiculo(@PathVariable Integer id,
                                                   @RequestParam("imagenes") MultipartFile[] imagenes) {
        try {
            VehiculoGetDTO dto = vehiculoService.subirImagenesVehiculo(id, imagenes);
            return new ResponseEntity<>(new ApiResponse<>("Imágenes subidas correctamente", dto, true), HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: " + e.getMessage(), null, false), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: " + e.getMessage(), null, false), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("Error al subir imágenes: " + e.getMessage(), null, false),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{vehiculoId}/imagen/{imagenId}")
    public ResponseEntity<?> verImagen(@PathVariable Integer vehiculoId, @PathVariable Integer imagenId) {
        try {
            ResponseEntity<Resource> response = vehiculoService.obtenerImagen(vehiculoId, imagenId);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response;
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ResponseEntity<>(new ApiResponse<>("Imagen no encontrada", null, false), HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(new ApiResponse<>("Error al obtener imagen", null, false), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("Error interno al obtener imagen: " + e.getMessage(), null, false),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{vehiculoId}/imagen/{imagenId}")
    public ResponseEntity<?> eliminarImagenVehiculo(@PathVariable Integer vehiculoId,
                                                    @PathVariable Integer imagenId) {
        try {
            VehiculoGetDTO dto = vehiculoService.eliminarImagenVehiculo(vehiculoId, imagenId);
            return new ResponseEntity<>(new ApiResponse<>("Imagen eliminada con éxito", dto, true), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: " + e.getMessage(), null, false), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: " + e.getMessage(), null, false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
