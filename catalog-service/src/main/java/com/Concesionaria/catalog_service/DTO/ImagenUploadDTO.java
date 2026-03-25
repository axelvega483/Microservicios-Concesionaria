package com.Concesionaria.catalog_service.DTO;

import org.springframework.web.multipart.MultipartFile;

public record ImagenUploadDTO(MultipartFile[] imagenes) {

}