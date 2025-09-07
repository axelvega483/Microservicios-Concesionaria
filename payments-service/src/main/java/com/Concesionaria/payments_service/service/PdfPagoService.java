package com.Concesionaria.payments_service.service;

import com.Concesionaria.payments_service.DTO.PagosGetDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfPagoService {

    @Value("${app.ruta.PDF}")
    private String rutaPdf;

    public String generarTicketPagoPDF(PagosGetDTO pago) {
        try {
            File directory = new File(rutaPdf);
            if (!directory.exists() && !directory.mkdirs()) {
                System.err.println("No se pudo crear la carpeta: " + rutaPdf);
                return null;
            }

            String pathArchivo = directory + File.separator + "ticket-pago-" + pago.getId() + ".pdf";
            Document document = new Document(new Rectangle(220, 400), 10, 10, 10, 10);
            PdfWriter.getInstance(document, new FileOutputStream(pathArchivo));
            document.open();

            // Fuentes
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font fontCampo = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
            Font fontValor = new Font(Font.FontFamily.HELVETICA, 9);

            // Encabezado
            Paragraph titulo = new Paragraph("🏢 Concesionaria", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(8f);
            document.add(titulo);

            // Fecha
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String fechaStr = pago.getFechaPago() != null ?
                    pago.getFechaPago().format(formatter) : "PENDIENTE";

            Paragraph fecha = new Paragraph(fechaStr, fontValor);
            fecha.setAlignment(Element.ALIGN_CENTER);
            fecha.setSpacingAfter(10f);
            document.add(fecha);

            // Separador
            document.add(new Paragraph("────────────────────────────"));

            // Datos en tabla
            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidths(new int[]{1, 2});
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(5f);
            tabla.setSpacingAfter(5f);

            agregarFila(tabla, "📄 ID Pago:", pago.getId().toString(), fontCampo, fontValor);
            agregarFila(tabla, "💵 Monto:", "$" + String.format("%.2f", pago.getMonto()), fontCampo, fontValor);
            agregarFila(tabla, "💳 Método:", pago.getMetodoPago().name(), fontCampo, fontValor);
            agregarFila(tabla, "📊 Estado:", pago.getEstado().name(), fontCampo, fontValor);

            document.add(tabla);
            document.add(new Paragraph("────────────────────────────"));

            // Mensaje de cierre
            Paragraph gracias = new Paragraph("¡Gracias por su pago!", new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));
            gracias.setAlignment(Element.ALIGN_CENTER);
            gracias.setSpacingBefore(8f);
            document.add(gracias);

            document.close();
            return pathArchivo;

        } catch (Exception e) {
            System.err.println("Error al generar el PDF: " + e.getMessage());
            return null;
        }
    }

    private void agregarFila(PdfPTable tabla, String campo, String valor, Font fontCampo, Font fontValor) {
        PdfPCell cellCampo = new PdfPCell(new Phrase(campo, fontCampo));
        cellCampo.setBorder(Rectangle.NO_BORDER);

        PdfPCell cellValor = new PdfPCell(new Phrase(valor, fontValor));
        cellValor.setBorder(Rectangle.NO_BORDER);

        tabla.addCell(cellCampo);
        tabla.addCell(cellValor);
    }
}
