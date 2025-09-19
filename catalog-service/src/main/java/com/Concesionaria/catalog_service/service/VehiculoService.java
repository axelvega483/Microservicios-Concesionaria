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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        if (existeVehiculo(post.getMarca(), post.getModelo(), post.getAnioModelo())) {
            Optional<Vehiculo> vehiculoOptional = buscarVehiculo(post.getMarca(), post.getModelo(), post.getAnioModelo());
            Vehiculo vehiculo = vehiculoOptional.get();
            Integer stock = vehiculo.getStock();
            vehiculo.setStock(stock + post.getStock());
            throw new EntityExistsException("El Vehiculo ya existe, se incremento stock");
        }
        Vehiculo vehiculo = mapper.create(post);
        Vehiculo saved = repo.save(vehiculo);
        return mapper.toDTO(saved);
    }

    @Override
    public VehiculoGetDTO updateVehiculo(Integer id, VehiculoPutDTO put) {
        Vehiculo vehiculo = repo.findById(id).orElse(null);
        if (vehiculo == null) {
            throw new EntityExistsException("El Vehiculo no existe");
        }
        vehiculo = mapper.update(vehiculo, put);
        Vehiculo saved = repo.save(vehiculo);
        return mapper.toDTO(saved);
    }


    @Override
    @CircuitBreaker(name = "sales-service", fallbackMethod = "findByVehiculoNoVenta")
    @Retry(name = "sales-service")
    public Optional<VehiculoGetDTO> findByIdVehiculo(Integer id) {
        Optional<Vehiculo> optVehi = repo.findById(id).filter(Vehiculo::getActivo);
        if (optVehi.isPresent()) {
            VehiculoGetDTO dto = mapper.toDTO(optVehi.get());
            List<Imagen> imagenes = imagenService.findByVehiculoId(id);
            dto.setImagenes(imagenes.stream()
                    .map(img -> new ImagenDTO(img.getId(), img.getNombre()))
                    .collect(Collectors.toList()));
            List<VehiculoVentaDetalleDTO> detalleVentas = venta.obtenerVentasPorVehiculo(dto.getId());
            dto.setDetalleVentas(detalleVentas);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    public Optional<VehiculoGetDTO> findByVehiculoNoVenta(Integer id, Throwable throwable) {
        System.err.println("Fallback ejecutado para findByVehiculoNoVenta(): " + throwable.getMessage());
        Optional<Vehiculo> optVehi = repo.findById(id).filter(Vehiculo::getActivo);
        if (optVehi.isPresent()) {
            VehiculoGetDTO dto = mapper.toDTO(optVehi.get());
            dto.setDetalleVentas(Collections.emptyList());
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    @CircuitBreaker(name = "sales-service", fallbackMethod = "findByAllVehiculonoVenta")
    @Retry(name = "sales-service")
    public List<VehiculoGetDTO> findAllVehiculo() {
        List<Vehiculo> vehiculos = repo.findAllActivo();
        List<VehiculoGetDTO> dtos = new ArrayList<>();
        for (Vehiculo vehiculo : vehiculos) {
            VehiculoGetDTO dto = mapper.toDTO(vehiculo);
            List<VehiculoVentaDetalleDTO> detalleVenta = venta.obtenerVentasPorVehiculo(vehiculo.getId());
            dto.setDetalleVentas(detalleVenta);
            dtos.add(dto);
        }
        return dtos;
    }

    public List<VehiculoGetDTO> findByAllVehiculonoVenta(Throwable throwable) {
        System.err.println("Fallback ejecutado para findByAllVehiculonoVenta(): " + throwable.getMessage());
        List<Vehiculo> vehiculos = repo.findAll();
        List<VehiculoGetDTO> dtos = new ArrayList<>();
        for (Vehiculo vehiculo : vehiculos) {
            VehiculoGetDTO dto = mapper.toDTO(vehiculo);
            dto.setDetalleVentas(Collections.emptyList());
            dtos.add(dto);
        }
        return dtos;
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

    public Optional<Vehiculo> findEntityByIdVehiculo(Integer id) {
        return repo.findById(id).filter(Vehiculo::getActivo);
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
    @CircuitBreaker(name = "sales-service", fallbackMethod = "eliminarImagenVehiculoFallback")
    @Retry(name = "sales-service")
    public VehiculoGetDTO eliminarImagenVehiculo(Integer vehiculoId, Integer imagenId) {
        Optional<Vehiculo> vehiculoOpt = findEntityByIdVehiculo(vehiculoId);
        if (vehiculoOpt.isEmpty()) {
            throw new EntityNotFoundException("Vehículo no encontrado con ID: " + vehiculoId);
        }

        Vehiculo vehiculo = vehiculoOpt.get();

        Optional<Imagen> imagenOpt = vehiculo.getImagenes().stream()
                .filter(img -> img.getId().equals(imagenId))
                .findFirst();

        if (imagenOpt.isEmpty()) {
            throw new EntityNotFoundException("Imagen no encontrada en el vehículo");
        }

        Imagen imagen = imagenOpt.get();

        imagenService.eliminarArchivoImagen(imagen.getNombre());

        vehiculo.getImagenes().remove(imagen);
        repo.save(vehiculo);

        imagenService.delete(imagenId);
        Vehiculo vehiculoActualizado = findEntityByIdVehiculo(vehiculoId)
                .orElseThrow(() -> new EntityNotFoundException("Error al obtener vehículo actualizado"));
        VehiculoGetDTO dto = mapper.toDTO(vehiculoActualizado);

        List<Imagen> imagenes = imagenService.findByVehiculoId(vehiculoId);
        dto.setImagenes(imagenes.stream()
                .map(img -> new ImagenDTO(img.getId(), img.getNombre()))
                .collect(Collectors.toList()));

        try {
            List<VehiculoVentaDetalleDTO> detalleVentas = venta.obtenerVentasPorVehiculo(vehiculoId);
            dto.setDetalleVentas(detalleVentas);
        } catch (Exception e) {
            dto.setDetalleVentas(Collections.emptyList());
        }

        return dto;
    }

    public VehiculoGetDTO eliminarImagenVehiculoFallback(Integer vehiculoId, Integer imagenId, Throwable throwable) {
        System.err.println("Fallback ejecutado para eliminarImagenVehiculoFallback(): " + throwable.getMessage());
        Optional<Vehiculo> vehiculoOpt = findEntityByIdVehiculo(vehiculoId);
        if (vehiculoOpt.isEmpty()) {
            throw new EntityNotFoundException("Vehículo no encontrado con ID: " + vehiculoId);
        }
        VehiculoGetDTO dto = mapper.toDTO(vehiculoOpt.get());
        List<Imagen> imagenes = imagenService.findByVehiculoId(vehiculoId);
        dto.setImagenes(imagenes.stream()
                .map(img -> new ImagenDTO(img.getId(), img.getNombre()))
                .collect(Collectors.toList()));
        dto.setDetalleVentas(Collections.emptyList());

        return dto;
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
