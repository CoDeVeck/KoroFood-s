package com.koroFoods.reservationService.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.koroFoods.reservationService.dto.ReporteReservaItem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j

public class PdfReservaService {

	public byte[] generarReporteReservas(List<ReporteReservaItem> reservas, LocalDate fechaInicio, LocalDate fechaFin) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			PdfWriter writer = new PdfWriter(baos);
			PdfDocument pdf = new PdfDocument(writer);
			Document document = new Document(pdf);

// Título
			Paragraph titulo = new Paragraph("REPORTE DE RESERVAS").setFontSize(18).setBold()
					.setTextAlignment(TextAlignment.CENTER);
			document.add(titulo);

// Subtítulo con rango de fechas
			String rangoFechas = String.format("Período: %s al %s",
					fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
					fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			Paragraph subtitulo = new Paragraph(rangoFechas).setFontSize(12).setTextAlignment(TextAlignment.CENTER)
					.setMarginBottom(20);
			document.add(subtitulo);

// Fecha de generación
			Paragraph fechaGeneracion = new Paragraph(
					"Generado el: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setFontSize(10)
					.setTextAlignment(TextAlignment.RIGHT).setMarginBottom(20);
			document.add(fechaGeneracion);

// Tabla
			float[] columnWidths = { 1, 2, 2, 1, 1, 2, 1, 1, 1.5f };
			Table table = new Table(UnitValue.createPercentArray(columnWidths));
			table.setWidth(UnitValue.createPercentValue(100));

// Headers
			agregarHeader(table, "ID");
			agregarHeader(table, "Cliente");
			agregarHeader(table, "Email");
			agregarHeader(table, "Mesa");
			agregarHeader(table, "Zona");
			agregarHeader(table, "Fecha/Hora");
			agregarHeader(table, "Personas");
			agregarHeader(table, "Estado");
			agregarHeader(table, "Depósito");

// Datos
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

			for (ReporteReservaItem item : reservas) {
				table.addCell(new Cell().add(new Paragraph(item.getIdReserva().toString())));
				table.addCell(new Cell().add(new Paragraph(item.getNombreCliente())));
				table.addCell(new Cell().add(new Paragraph(item.getEmailCliente())));
				table.addCell(new Cell().add(new Paragraph(item.getNumeroMesa().toString())));
				table.addCell(new Cell().add(new Paragraph(item.getZona())));
				table.addCell(new Cell().add(new Paragraph(item.getFechaHora().format(formatter))));
				table.addCell(new Cell().add(new Paragraph(item.getEstadoDescripcion())));
				table.addCell(new Cell()
						.add(new Paragraph(item.getMontoDeposito() != null ? "S/ " + item.getMontoDeposito() : "-")));
			}

			document.add(table);

// Resumen
			Paragraph resumen = new Paragraph(String.format("\nTotal de reservas: %d", reservas.size())).setFontSize(12)
					.setBold().setMarginTop(20);
			document.add(resumen);

			document.close();

			log.info("✅ PDF de reservas generado exitosamente con {} registros", reservas.size());
			return baos.toByteArray();

		} catch (Exception e) {
			log.error("❌ Error al generar PDF de reservas", e);
			throw new RuntimeException("Error al generar el reporte PDF", e);
		}
	}

	private void agregarHeader(Table table, String texto) {
		Cell header = new Cell().add(new Paragraph(texto).setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY)
				.setTextAlignment(TextAlignment.CENTER);
		table.addHeaderCell(header);
	}
}
