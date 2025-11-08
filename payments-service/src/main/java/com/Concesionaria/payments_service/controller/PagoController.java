package com.Concesionaria.payments_service.controller;

import com.Concesionaria.payments_service.DTO.GenerarPagosRequestDTO;
import com.Concesionaria.payments_service.DTO.PagosGetDTO;
import com.Concesionaria.payments_service.DTO.PagosPutDTO;
import com.Concesionaria.payments_service.service.IPagosService;
import com.Concesionaria.payments_service.service.PdfPagoService;
import com.Concesionaria.payments_service.util.MetodoPago;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.io.FileNotFoundException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin("*")
@RequestMapping("pagos")
@Tag(name = "Pagos", description = "Controlador para operaciones de pagos")
public class PagoController {

    @Autowired
    private IPagosService pagosService;

    @Autowired
    private PdfPagoService pdf;

    @Operation(summary = "Generar pagos para una venta", description = "Genera los pagos (cuotas) asociados a una venta específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pagos generados exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/generar")
    public ResponseEntity<List<PagosGetDTO>> generarPagos(
            @Parameter(description = "Solicitud para generar pagos", required = true)
            @RequestBody GenerarPagosRequestDTO request) {
        List<PagosGetDTO> pagosGenerados = pagosService.generarPagos(request);
        return new ResponseEntity<>(pagosGenerados, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todos los pagos", description = "Devuelve una lista con todos los pagos registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagos listados correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<PagosGetDTO>> listarPagos() {
        List<PagosGetDTO> dto = pagosService.findAll();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Obtener pago por ID", description = "Devuelve un pago específico basado en su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("{id}")
    public ResponseEntity<PagosGetDTO> obtenerPago(
            @Parameter(description = "ID del pago a buscar", example = "1", required = true)
            @PathVariable Integer id) {
        PagosGetDTO pago = pagosService.findById(id);
        return new ResponseEntity<>(pago, HttpStatus.OK);
    }

    @Operation(summary = "Confirmar pago", description = "Confirma un pago con el método de pago especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago confirmado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/confirmar/{id}")
    public ResponseEntity<PagosGetDTO> confirmarPago(
            @Parameter(description = "ID del pago a confirmar", example = "1", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Método de pago utilizado", required = true)
            @RequestParam MetodoPago metodoPago) {
        PagosGetDTO pagoConfirmado = pagosService.confirmarPago(id, metodoPago);
        return new ResponseEntity<>(pagoConfirmado, HttpStatus.OK);
    }

    @Operation(summary = "Actualizar pago existente", description = "Actualiza la información de un pago existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("{id}")
    public ResponseEntity<PagosGetDTO> actualizarPago(
            @Parameter(description = "ID del pago a actualizar", example = "1", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Datos actualizados del pago", required = true)
            @RequestBody PagosPutDTO putDTO) {
        PagosGetDTO pagoActualizado = pagosService.update(id, putDTO);
        return new ResponseEntity<>(pagoActualizado, HttpStatus.OK);
    }

    @Operation(summary = "Cancelar pago", description = "Cancela o anula un pago existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago cancelado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("{id}")
    public ResponseEntity<String> cancelarPago(
            @Parameter(description = "ID del pago a cancelar", example = "1", required = true)
            @PathVariable Integer id) {
        pagosService.delete(id);
        return new ResponseEntity<>("Pago anulado", HttpStatus.OK);
    }

    @Operation(summary = "Obtener pagos por ventas", description = "Devuelve los pagos asociados a una lista de ventas específicas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagos encontrados exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/ventas")
    public ResponseEntity<List<PagosGetDTO>> getPagosPorVentas(
            @Parameter(description = "Lista de IDs de ventas", required = true)
            @RequestParam List<Integer> ventaIds) {
        List<PagosGetDTO> pagos = pagosService.getPagosPorVentas(ventaIds);
        return new ResponseEntity<>(pagos, HttpStatus.OK);
    }

    @Operation(summary = "Descargar ticket de pago en PDF", description = "Genera y descarga el ticket de pago en formato PDF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket de pago generado y descargado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error al generar el PDF")
    })
    @GetMapping("/ticket/{id}")
    public ResponseEntity<InputStreamResource> downloadPDF(
            @Parameter(description = "ID del pago para generar ticket", example = "1", required = true)
            @PathVariable Integer id) {
        PagosGetDTO pago = pagosService.findById(id);

        String filePath = pdf.generarTicketPagoPDF(pago);

        try {
            FileInputStream fis = new FileInputStream(filePath);
            String fileName = "ticket-pago-" + pago.getId() + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(fis));
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}