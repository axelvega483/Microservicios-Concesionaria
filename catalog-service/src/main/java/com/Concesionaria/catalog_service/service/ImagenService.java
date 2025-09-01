package com.Concesionaria.catalog_service.service;

import com.Concesionaria.catalog_service.model.Imagen;
import com.Concesionaria.catalog_service.model.Vehiculo;
import com.Concesionaria.catalog_service.repository.ImagenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ImagenService implements IServiceImagen{
    @Autowired
    private ImagenRepository repo;

    @Value("${app.ruta.imagenes}")
    private String rutaImagenes;

    @Override
    public Imagen save(Imagen imagen) {
        return repo.save(imagen);
    }

    @Override
    public Optional<Imagen> findById(Integer id) {
        return repo.findById(id);
    }

    @Override
    public List<Imagen> findAll() {
        return repo.findAll();
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    @Override
    public List<Imagen> findByVehiculoId(Integer vehiculoId) {
        return repo.findByVehiculoId(vehiculoId);
    }

    @Override
    public void deleteByVehiculoId(Integer vehiculoId) {
        repo.deleteByVehiculoId(vehiculoId);
    }

    private static final List<String> EXTENSIONES_PERMITIDAS = List.of("jpg", "jpeg", "png", "gif");

    @Override
    public List<Imagen> procesarImagenes(MultipartFile[] archivos, Vehiculo vehiculo) {
        Path directorioPath = Paths.get(rutaImagenes);
        crearDirectorioSiNoExiste(directorioPath);

        List<Imagen> imagenesGuardadas = new ArrayList<>();

        for (MultipartFile archivo : archivos) {
            try {
                String extension = obtenerExtensionArchivo(archivo.getOriginalFilename());

                if (!EXTENSIONES_PERMITIDAS.contains(extension.toLowerCase())) {
                    throw new IllegalArgumentException("Formato de imagen no permitido: " + extension);
                }

                String nombreArchivo = generarNombreArchivo(archivo.getOriginalFilename());
                Path rutaCompleta = directorioPath.resolve(nombreArchivo);

                // Guardar archivo físico
                Files.write(rutaCompleta, archivo.getBytes());

                // Guardar en base de datos
                Imagen imagen = new Imagen();
                imagen.setNombre(nombreArchivo);
                imagen.setVehiculo(vehiculo);
                Imagen imagenGuardada = save(imagen);

                imagenesGuardadas.add(imagenGuardada);

            } catch (IOException e) {
                throw new RuntimeException("Error al procesar imagen: " + e.getMessage(), e);
            }
        }

        return imagenesGuardadas;
    }

    @Override
    public void eliminarArchivoImagen(String nombreArchivo) {
        try {
            Path rutaArchivo = Paths.get(rutaImagenes).resolve(nombreArchivo).toAbsolutePath();
            Files.deleteIfExists(rutaArchivo);
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar archivo de imagen: " + e.getMessage(), e);
        }
    }

    @Override
    public Resource obtenerArchivoImagen(String nombreArchivo) throws IOException {
        Path rutaArchivo = Paths.get(rutaImagenes).resolve(nombreArchivo).toAbsolutePath();
        Resource recurso = new UrlResource(rutaArchivo.toUri());

        if (!recurso.exists() || !recurso.isReadable()) {
            throw new IOException("No se puede leer el archivo: " + nombreArchivo);
        }

        return recurso;
    }

    @Override
    public String obtenerExtensionArchivo(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1).toLowerCase();
    }

    private void crearDirectorioSiNoExiste(Path directorio) {
        try {
            if (!Files.exists(directorio)) {
                Files.createDirectories(directorio);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al crear directorio de imágenes: " + e.getMessage(), e);
        }
    }

    private String generarNombreArchivo(String nombreOriginal) {
        String extension = obtenerExtensionArchivo(nombreOriginal);
        return UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
    }

}
