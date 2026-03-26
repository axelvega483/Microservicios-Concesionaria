package com.Concesionaria.payments_service.controller;

import com.Concesionaria.payments_service.DTO.GenerarPagosRequestDTO;
import com.Concesionaria.payments_service.DTO.PagosGetDTO;
import com.Concesionaria.payments_service.DTO.PagosPutDTO;
import com.Concesionaria.payments_service.service.IPagosService;
import com.Concesionaria.payments_service.service.PdfPagoService;
import com.Concesionaria.payments_service.util.MetodoPago;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@CrossOrigin("*")
@RequestMapping("payments")
public class PagoController {

    @Autowired
    private IPagosService pagosService;

    @Autowired
    private PdfPagoService pdf;


    @PostMapping("/generar")
    public ResponseEntity<?> generarPagos(@RequestBody GenerarPagosRequestDTO request) {
        List<PagosGetDTO> pagosGenerados = pagosService.generarPagos(request);
        return new ResponseEntity<>(pagosGenerados, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<?> listarPagos() {
        List<PagosGetDTO> dto = pagosService.findAll();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @GetMapping("{id}")
    public ResponseEntity<?> obtenerPago(@PathVariable Integer id) {
        PagosGetDTO pago = pagosService.findById(id);
        return new ResponseEntity<>(pago, HttpStatus.OK);
    }


    @PutMapping("/confirmar/{id}")
    public ResponseEntity<?> confirmarPago(@PathVariable Integer id, @RequestParam MetodoPago metodoPago) {
        PagosGetDTO pagoConfirmado = pagosService.confirmarPago(id, metodoPago);
        return new ResponseEntity<>(pagoConfirmado, HttpStatus.OK);
    }


    @DeleteMapping("{id}")
    public ResponseEntity<String> cancelarPago(@PathVariable Integer id) {
        pagosService.delete(id);
        return new ResponseEntity<>("Pago cancelado", HttpStatus.OK);
    }


    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<PagosGetDTO>> getPagosPorVenta(@PathVariable Integer ventaId) {
        List<PagosGetDTO> pagos = pagosService.getPagosPorVenta(ventaId);
        return ResponseEntity.ok(pagos);
    }

    @PutMapping("/{pagoId}/anular")
    public ResponseEntity<?> anularPago(@PathVariable Integer pagoId, HttpServletRequest request) {
        log.info("=== ANULAR PAGO ===");
        log.info("Pago ID: {}", pagoId);
        log.info("Headers: {}", Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(h -> h, request::getHeader)));

        pagosService.anularPago(pagoId);
        return ResponseEntity.ok("Pago anulado correctamente");
    }


    @GetMapping("/ticket/{id}")
    public ResponseEntity<InputStreamResource> downloadPDF(@PathVariable Integer id) {
        PagosGetDTO pago = pagosService.findById(id);

        String filePath = pdf.generarTicketPagoPDF(pago);

        try {
            FileInputStream fis = new FileInputStream(filePath);
            String fileName = "ticket-pago-" + pago.id() + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(fis));
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}