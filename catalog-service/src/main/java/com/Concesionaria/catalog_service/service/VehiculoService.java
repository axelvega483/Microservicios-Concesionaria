package com.Concesionaria.catalog_service.service;

import com.Concesionaria.catalog_service.DTO.*;
import com.Concesionaria.catalog_service.model.Imagen;
import com.Concesionaria.catalog_service.model.Vehiculo;
import com.Concesionaria.catalog_service.repository.VehiculoRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/*
@Service
@Transactional
public class VehiculoService implements IVehiculoService {
    @Autowired
    private VehiculoRepository repo;

    @Autowired
    private ImagenService imagenService;

    @Autowired
    private VentaFeignClient venta;

    @Autowired
    private MapperDTO mapper;


    public Boolean existeVehiculo(String marca, String modelo, Integer anioModelo) {
        return repo.findByMarcaModeloAnioModelo(marca, modelo, anioModelo).isPresent();
    }

    public Optional<Vehiculo> buscarVehiculo(String marca, String modelo, Integer anioModelo) {
        return repo.findByMarcaModeloAnioModelo(marca, modelo, anioModelo);
    }

    @Override
    public VehiculoGetDTO createVehiculo(VehiculoPostDTO post) {
        if (existeVehiculo(post.marca(), post.modelo(), post.anioModelo())) {
            Optional<Vehiculo> vehiculoOptional = buscarVehiculo(post.marca(), post.modelo(), post.anioModelo());
            Vehiculo vehiculo = vehiculoOptional.get();
            Integer stock = vehiculo.getStock();
            vehiculo.setStock(stock + post.stock());
            throw new EntityExistsException("El Vehiculo ya existe, se incremento stock");
        }
        Vehiculo vehiculo = mapper.toEntity(post);
        Vehiculo saved = repo.save(vehiculo);
        return mapper.toDTO(saved);
    }

    @Override
    public VehiculoGetDTO updateVehiculo(Integer id, VehiculoPutDTO put) {
        Vehiculo vehiculo = repo.findById(id).orElse(null);
        if (vehiculo == null) {
            throw new EntityExistsException("El Vehiculo no existe");
        }
        mapper.update(vehiculo, put);
        vehiculo.setActivo(true);
        Vehiculo saved = repo.save(vehiculo);
        return mapper.toDTO(saved);
    }


    @Override
    @CircuitBreaker(name = "sales-service", fallbackMethod = "findByVehiculoNoVenta")
    @Retry(name = "sales-service")
    public Optional<VehiculoGetDTO> findByIdVehiculo(Integer id) {
        Optional<Vehiculo> optVehi = repo.findById(id).filter(Vehiculo::isActivo);
        if (optVehi.isPresent()) {
            Vehiculo vehiculo = optVehi.get();
            List<VehiculoVentaDetalleDTO> detalleVentas = venta.obtenerVentasPorVehiculo(vehiculo.getId());
            VehiculoGetDTO dto = mapper.toDTO(vehiculo, detalleVentas);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    public Optional<VehiculoGetDTO> findByVehiculoNoVenta(Integer id, Throwable throwable) {
        System.err.println("Fallback ejecutado para findByVehiculoNoVenta(): " + throwable.getMessage());
        Optional<Vehiculo> optVehi = repo.findById(id).filter(Vehiculo::isActivo);
        if (optVehi.isPresent()) {
            Vehiculo vehiculo = optVehi.get();
            VehiculoGetDTO dto = mapper.toDTO(vehiculo);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    public Optional<VehiculoGetDTO> vehiculoId(Integer id) {
        Optional<Vehiculo> optVehi = repo.findById(id).filter(Vehiculo::isActivo);
        if (optVehi.isPresent()) {
            Vehiculo vehiculo = optVehi.get();
            VehiculoGetDTO dto = mapper.toDTO(vehiculo);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    public List<VehiculoGetDTO> findAllVehiculo() {
        return mapper.toDTOList(repo.findAll());
    }

    @Override
    public void deleteByIdVehiculo(Integer id) {
        Optional<Vehiculo> vehiOpt = repo.findById(id);
        if (vehiOpt.isPresent()) {
            Vehiculo vehiculo = vehiOpt.get();
            vehiculo.setActivo(Boolean.FALSE);
            repo.save(vehiculo);
        }
    }

    @Override
    public VehiculoGetDTO incrementarStock(Integer vehiculoId, Integer cantidad) {
        Vehiculo vehiculo = repo.findById(vehiculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado con ID: " + vehiculoId));
        vehiculo.setStock(vehiculo.getStock() + cantidad);
        Vehiculo vehiculoActualizado = repo.save(vehiculo);
        return mapper.toDTO(vehiculoActualizado);
    }
    @Override
    public VehiculoGetDTO descontarStock(Integer vehiculoId, Integer cantidad) {
        Vehiculo vehiculo = repo.findById(vehiculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado con ID: " + vehiculoId));
        vehiculo.setStock(vehiculo.getStock() - cantidad);
        Vehiculo vehiculoActualizado = repo.save(vehiculo);
        return mapper.toDTO(vehiculoActualizado);
    }

    public Optional<Vehiculo> findEntityByIdVehiculo(Integer id) {
        return repo.findById(id).filter(Vehiculo::isActivo);
    }

    @Override
    public VehiculoGetDTO subirImagenesVehiculo(Integer vehiculoId, MultipartFile[] imagenes) {
        Optional<Vehiculo> vehiculoOpt = findEntityByIdVehiculo(vehiculoId);
        if (vehiculoOpt.isEmpty()) {
            throw new EntityNotFoundException("Vehículo no encontrado con ID: " + vehiculoId);
        }

        Vehiculo vehiculo = vehiculoOpt.get();
        List<Imagen> imagenesGuardadas = imagenService.procesarImagenes(imagenes, vehiculo);

        vehiculo.getImagenes().addAll(imagenesGuardadas);
        Vehiculo vehiculoActualizado = repo.save(vehiculo);

        return mapper.toDTO(vehiculoActualizado);
    }

    @Override
    public ResponseEntity<Resource> obtenerImagen(Integer vehiculoId, Integer imagenId) {
        try {
            Optional<Imagen> imagenOpt = imagenService.findById(imagenId);
            if (imagenOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Imagen imagen = imagenOpt.get();
            if (!imagen.getVehiculo().getId().equals(vehiculoId)) {
                return ResponseEntity.notFound().build();
            }
            Resource recurso = imagenService.obtenerArchivoImagen(imagen.getNombre());

            if (!recurso.exists() || !recurso.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            String extension = imagenService.obtenerExtensionArchivo(imagen.getNombre());
            MediaType mediaType = determinarMediaType(extension);

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(recurso);

        } catch (IOException e) {
            System.err.println("Error al obtener imagen: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            // Log del error para debugging
            System.err.println("Error inesperado al obtener imagen: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public VehiculoGetDTO eliminarImagenVehiculo(Integer vehiculoId, Integer imagenId) {
        Vehiculo vehiculo = findEntityByIdVehiculo(vehiculoId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Vehículo no encontrado con ID: " + vehiculoId));
        Imagen imagen = vehiculo.getImagenes()
                .stream()
                .filter(img -> img.getId().equals(imagenId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Imagen no encontrada en el vehículo con ID: " + imagenId));

        try {
            imagenService.eliminarArchivoImagen(imagen.getNombre());
        } catch (Exception e) {
            System.err.println("Error al eliminar archivo de imagen: " + e.getMessage());

        }
        imagenService.delete(imagenId);
        vehiculo.getImagenes().removeIf(img -> img.getId().equals(imagenId));

        Vehiculo vehiculoActualizado = repo.save(vehiculo);

        return mapper.toDTO(vehiculoActualizado);
    }

    private MediaType determinarMediaType(String extension) {
        return switch (extension.toLowerCase()) {
            case "png" -> MediaType.IMAGE_PNG;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "gif" -> MediaType.IMAGE_GIF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

}
*/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class VehiculoService implements IVehiculoService {

    private static final Logger log = LoggerFactory.getLogger(VehiculoService.class);

    @Autowired
    private VehiculoRepository repo;

    @Autowired
    private ImagenService imagenService;

    @Autowired
    private VentaFeignClient venta;

    @Autowired
    private MapperDTO mapper;

    // Métodos auxiliares privados
    private Boolean existeVehiculo(String marca, String modelo, Integer anioModelo) {
        return repo.findByMarcaAndModeloAndAnioModeloAndActivoTrue(marca, modelo, anioModelo).isPresent();
    }

    private Optional<Vehiculo> buscarVehiculo(String marca, String modelo, Integer anioModelo) {
        return repo.findByMarcaAndModeloAndAnioModeloAndActivoTrue(marca, modelo, anioModelo);
    }

    @Override
    public VehiculoGetDTO createVehiculo(VehiculoPostDTO post) {
        log.info("Creando nuevo vehículo: {} {} {}", post.marca(), post.modelo(), post.anioModelo());

        if (existeVehiculo(post.marca(), post.modelo(), post.anioModelo())) {
            log.warn("Vehículo ya existe, incrementando stock: {} {} {}", post.marca(), post.modelo(), post.anioModelo());
            Vehiculo vehiculo = buscarVehiculo(post.marca(), post.modelo(), post.anioModelo()).get();
            Integer nuevoStock = vehiculo.getStock() + post.stock();
            vehiculo.setStock(nuevoStock);
            Vehiculo saved = repo.save(vehiculo);
            log.info("Stock incrementado de {} a {} para vehículo ID: {}",
                    vehiculo.getStock() - post.stock(), nuevoStock, saved.getId());
            return mapper.toDTO(saved);
        }

        Vehiculo vehiculo = mapper.toEntity(post);
        Vehiculo saved = repo.save(vehiculo);
        log.info("Vehículo creado exitosamente con ID: {}", saved.getId());
        return mapper.toDTO(saved);
    }

    @Override
    public VehiculoGetDTO updateVehiculo(Integer id, VehiculoPutDTO put) {
        log.info("Actualizando vehículo con ID: {}", id);

        Vehiculo vehiculo = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El vehículo con ID " + id + " no existe"));

        mapper.update(vehiculo, put);
        // Mantener el estado activo si estaba activo, no forzar a true
            vehiculo.setActivo(true);


        Vehiculo saved = repo.save(vehiculo);
        log.info("Vehículo actualizado exitosamente: {}", id);
        return mapper.toDTO(saved);
    }

    @Override
    @CircuitBreaker(name = "sales-service", fallbackMethod = "findByIdVehiculoFallback")
    @Retry(name = "sales-service")
    public Optional<VehiculoGetDTO> findByIdVehiculo(Integer id) {
        log.info("Buscando vehículo con ID: {} (con ventas)", id);

        Optional<Vehiculo> optVehi = repo.findById(id).filter(Vehiculo::isActivo);
        if (optVehi.isPresent()) {
            Vehiculo vehiculo = optVehi.get();
            try {
                List<VehiculoVentaDetalleDTO> detalleVentas = venta.obtenerVentasPorVehiculo(vehiculo.getId());
                VehiculoGetDTO dto = mapper.toDTO(vehiculo, detalleVentas);
                return Optional.of(dto);
            } catch (Exception e) {
                log.error("Error al obtener ventas para vehículo {}: {}", id, e.getMessage());
                // Fallback manual si falla el feign
                return Optional.of(mapper.toDTO(vehiculo));
            }
        }
        return Optional.empty();
    }

    public Optional<VehiculoGetDTO> findByIdVehiculoFallback(Integer id, Throwable throwable) {
        log.warn("Fallback ejecutado para findByIdVehiculo({}): {}", id, throwable.getMessage());

        Optional<Vehiculo> optVehi = repo.findById(id).filter(Vehiculo::isActivo);
        if (optVehi.isPresent()) {
            Vehiculo vehiculo = optVehi.get();
            VehiculoGetDTO dto = mapper.toDTO(vehiculo);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    public Optional<VehiculoGetDTO> vehiculoId(Integer id) {
        log.debug("Buscando vehículo con ID: {} (sin ventas)", id);

        Optional<Vehiculo> optVehi = repo.findById(id).filter(Vehiculo::isActivo);
        if (optVehi.isPresent()) {
            Vehiculo vehiculo = optVehi.get();
            VehiculoGetDTO dto = mapper.toDTO(vehiculo);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    public List<VehiculoGetDTO> findAllVehiculo() {
        log.debug("Obteniendo todos los vehículos activos");
        List<Vehiculo> vehiculos = repo.findByActivoTrue();
        return mapper.toDTOList(vehiculos);
    }

    @Override
    public void deleteByIdVehiculo(Integer id) {
        log.info("Eliminando (soft delete) vehículo con ID: {}", id);

        Optional<Vehiculo> vehiOpt = repo.findById(id);
        if (vehiOpt.isPresent()) {
            Vehiculo vehiculo = vehiOpt.get();
            vehiculo.setActivo(Boolean.FALSE);
            repo.save(vehiculo);
            log.info("Vehículo {} marcado como inactivo", id);
        } else {
            log.warn("Intento de eliminar vehículo inexistente: {}", id);
            throw new EntityNotFoundException("Vehículo no encontrado con ID: " + id);
        }
    }

    @Override
    public VehiculoGetDTO incrementarStock(Integer vehiculoId, Integer cantidad) {
        log.info("Incrementando stock del vehículo {} en {} unidades", vehiculoId, cantidad);

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a incrementar debe ser positiva");
        }

        Vehiculo vehiculo = repo.findById(vehiculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado con ID: " + vehiculoId));

        Integer stockAnterior = vehiculo.getStock();
        vehiculo.setStock(stockAnterior + cantidad);
        Vehiculo vehiculoActualizado = repo.save(vehiculo);

        log.info("Stock actualizado: {} → {} para vehículo {}", stockAnterior, vehiculo.getStock(), vehiculoId);
        return mapper.toDTO(vehiculoActualizado);
    }

    @Override
    public VehiculoGetDTO descontarStock(Integer vehiculoId, Integer cantidad) {
        log.info("Descontando stock del vehículo {} en {} unidades", vehiculoId, cantidad);

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a descontar debe ser positiva");
        }

        Vehiculo vehiculo = repo.findById(vehiculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado con ID: " + vehiculoId));

        Integer stockAnterior = vehiculo.getStock();
        if (stockAnterior < cantidad) {
            throw new IllegalArgumentException(
                    String.format("Stock insuficiente para vehículo ID %d. Stock actual: %d, Cantidad solicitada: %d",
                            vehiculoId, stockAnterior, cantidad)
            );
        }

        vehiculo.setStock(stockAnterior - cantidad);
        Vehiculo vehiculoActualizado = repo.save(vehiculo);

        log.info("Stock actualizado: {} → {} para vehículo {}", stockAnterior, vehiculo.getStock(), vehiculoId);
        return mapper.toDTO(vehiculoActualizado);
    }

    public Optional<Vehiculo> findEntityByIdVehiculo(Integer id) {
        return repo.findById(id).filter(Vehiculo::isActivo);
    }

    @Override
    public VehiculoGetDTO subirImagenesVehiculo(Integer vehiculoId, MultipartFile[] imagenes) {
        log.info("Subiendo {} imágenes para vehículo ID: {}", imagenes.length, vehiculoId);

        Optional<Vehiculo> vehiculoOpt = findEntityByIdVehiculo(vehiculoId);
        if (vehiculoOpt.isEmpty()) {
            throw new EntityNotFoundException("Vehículo no encontrado con ID: " + vehiculoId);
        }

        Vehiculo vehiculo = vehiculoOpt.get();
        List<Imagen> imagenesGuardadas = imagenService.procesarImagenes(imagenes, vehiculo);

        vehiculo.getImagenes().addAll(imagenesGuardadas);
        Vehiculo vehiculoActualizado = repo.save(vehiculo);

        log.info("Imágenes subidas exitosamente para vehículo {}", vehiculoId);
        return mapper.toDTO(vehiculoActualizado);
    }

    @Override
    public ResponseEntity<Resource> obtenerImagen(Integer vehiculoId, Integer imagenId) {
        log.debug("Obteniendo imagen {} para vehículo {}", imagenId, vehiculoId);

        try {
            Optional<Imagen> imagenOpt = imagenService.findById(imagenId);
            if (imagenOpt.isEmpty()) {
                log.warn("Imagen no encontrada: {}", imagenId);
                return ResponseEntity.notFound().build();
            }

            Imagen imagen = imagenOpt.get();
            if (!imagen.getVehiculo().getId().equals(vehiculoId)) {
                log.warn("Imagen {} no pertenece al vehículo {}", imagenId, vehiculoId);
                return ResponseEntity.notFound().build();
            }

            Resource recurso = imagenService.obtenerArchivoImagen(imagen.getNombre());

            if (!recurso.exists() || !recurso.isReadable()) {
                log.error("Archivo de imagen no legible o no existe: {}", imagen.getNombre());
                return ResponseEntity.notFound().build();
            }

            String extension = imagenService.obtenerExtensionArchivo(imagen.getNombre());
            MediaType mediaType = determinarMediaType(extension);

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(recurso);

        } catch (IOException e) {
            log.error("Error al obtener imagen: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            log.error("Error inesperado al obtener imagen: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public VehiculoGetDTO eliminarImagenVehiculo(Integer vehiculoId, Integer imagenId) {
        log.info("Eliminando imagen {} del vehículo {}", imagenId, vehiculoId);

        Vehiculo vehiculo = findEntityByIdVehiculo(vehiculoId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Vehículo no encontrado con ID: " + vehiculoId));

        Imagen imagen = vehiculo.getImagenes()
                .stream()
                .filter(img -> img.getId().equals(imagenId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Imagen no encontrada en el vehículo con ID: " + imagenId));

        try {
            imagenService.eliminarArchivoImagen(imagen.getNombre());
            log.debug("Archivo de imagen eliminado: {}", imagen.getNombre());
        } catch (Exception e) {
            log.error("Error al eliminar archivo de imagen: {}", e.getMessage(), e);
            // Continuamos con la eliminación lógica aunque falle la física
        }

        imagenService.delete(imagenId);
        vehiculo.getImagenes().removeIf(img -> img.getId().equals(imagenId));

        Vehiculo vehiculoActualizado = repo.save(vehiculo);
        log.info("Imagen {} eliminada del vehículo {}", imagenId, vehiculoId);

        return mapper.toDTO(vehiculoActualizado);
    }

    private MediaType determinarMediaType(String extension) {
        return switch (extension.toLowerCase()) {
            case "png" -> MediaType.IMAGE_PNG;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "gif" -> MediaType.IMAGE_GIF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}