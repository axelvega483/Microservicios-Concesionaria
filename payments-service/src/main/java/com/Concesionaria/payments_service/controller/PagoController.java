package com.Concesionaria.payments_service.controller;

import com.Concesionaria.payments_service.DTO.GenerarPagosRequestDTO;
import com.Concesionaria.payments_service.DTO.PagosGetDTO;
import com.Concesionaria.payments_service.DTO.PagosPutDTO;
import com.Concesionaria.payments_service.service.IPagosService;
import com.Concesionaria.payments_service.service.PdfPagoService;
import com.Concesionaria.payments_service.util.MetodoPago;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin("*")
@RequestMapping("pagos")
public class PagoController {
    @Autowired
    private IPagosService pagosService;

    @Autowired
    private PdfPagoService pdf;


    @Value("${app.ruta.PDF}")
    private String RUTA_PDF;

    @PostMapping("/generar")
    public ResponseEntity<?> generarPagos(@RequestBody GenerarPagosRequestDTO request) {
        try {
            return new ResponseEntity<>(pagosService.generarPagos(request), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> listarPagos() {
        try {
            List<PagosGetDTO> dto = pagosService.findAll();
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> obtenerPago(@PathVariable Integer id) {
        try {
            PagosGetDTO pago = pagosService.findById(id);
            return new ResponseEntity<>(pago, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/confirmar/{id}")
    public ResponseEntity<?> confirmarPago(@PathVariable Integer id, @RequestParam MetodoPago metodoPago) {
        try {
            PagosGetDTO pagoConfirmado = pagosService.confirmarPago(id, metodoPago);
            return new ResponseEntity<>(pagoConfirmado, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> actualizarPago(@PathVariable Integer id, @RequestBody PagosPutDTO putDTO) {
        try {
            PagosGetDTO pagoActualizado = pagosService.update(id, putDTO);
            return new ResponseEntity<>(pagoActualizado, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> cancelarPago(@PathVariable Integer id) {
        try {
            pagosService.delete(id);
            return new ResponseEntity<>("Pago anulado", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ventas")
    public ResponseEntity<?> getPagosPorVentas(@RequestParam List<Integer> ventaIds) {
        try {
            List<PagosGetDTO> pagos = pagosService.getPagosPorVentas(ventaIds);
            return new ResponseEntity<>(pagos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/ticket/{id}")
    public ResponseEntity<InputStreamResource> downloadPDF(@PathVariable Integer id) {
        try {
            PagosGetDTO pago = pagosService.findById(id);
            if (pago == null) {
                return ResponseEntity.notFound().build();
            }

            String filePath = pdf.generarTicketPagoPDF(pago);
            if (filePath == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            FileInputStream fis = new FileInputStream(filePath);
            String fileName = "ticket-pago-" + pago.getId() + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(fis));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}