package com.koroFoods.menuService.service;

import com.koroFoods.menuService.dto.PlatoDtoFeign;
import com.koroFoods.menuService.dto.ResultadoResponse;
import com.koroFoods.menuService.model.Plato;
import com.koroFoods.menuService.repository.IMenuRepository;

import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import org.springframework.stereotype.Service;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

@Service
@RequiredArgsConstructor
public class MenuService {

	private final IMenuRepository menuRepository;

	// Método para restar el stock de los pedidos consumidos
	public ResultadoResponse<PlatoDtoFeign> substractStockOrder(Integer idPlato, Integer cantidadVendida) {
        Plato plato = menuRepository.findById(idPlato)
                .orElseThrow(() -> new RuntimeException("El plato no existe"));
        if (plato.getStock() < cantidadVendida) {
            return ResultadoResponse.error("Stock insuficiente para el plato: " + plato.getNombre());
        }
        plato.setStock(plato.getStock() - cantidadVendida);
        menuRepository.save(plato);
        PlatoDtoFeign dto = new PlatoDtoFeign();
        dto.setIdPlato(plato.getIdPlato());
        dto.setNombre(plato.getNombre());
        dto.setPrecio(plato.getPrecio());
        dto.setStock(plato.getStock());

        return ResultadoResponse.success("Stock actualizado", dto);
    }
    
	// Método para el feign de la reseña
	public ResultadoResponse<List<PlatoDtoFeign>> getAllDish() {
		List<Plato> platos = menuRepository.findAllByTipoPlato();
		List<PlatoDtoFeign> dtos = platos.stream().map(plato -> {
			PlatoDtoFeign dto = new PlatoDtoFeign();
			dto.setIdPlato(plato.getIdPlato());
			dto.setNombre(plato.getNombre());
			dto.setTipoPlato(plato.getTipoPlato().toString());
			dto.setImagen(plato.getImagen());
			dto.setPrecio(plato.getPrecio());
			return dto;
		}).toList();
		return ResultadoResponse.success("Eventos encontrados", dtos);
	}

	public ResultadoResponse<PlatoDtoFeign> getDishById(Integer id) {
		Plato dish = menuRepository.findById(id).orElseThrow(() -> new RuntimeException("Plato no encontrado"));

		PlatoDtoFeign dto = new PlatoDtoFeign();
		dto.setIdPlato(dish.getIdPlato());
		dto.setNombre(dish.getNombre());
		dto.setTipoPlato(dish.getTipoPlato().toString());
		dto.setImagen(dish.getImagen());
		dto.setPrecio(dish.getPrecio());
		return ResultadoResponse.success("Plato encontrado", dto);
	}

	public byte[] generateMenuPdf(List<PlatoDtoFeign> platos) throws Exception {
		Document document = new Document(PageSize.A4);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			PdfWriter writer = PdfWriter.getInstance(document, baos);

			writer.setPageEvent(new PdfPageEventHelper() {
				@Override
				public void onEndPage(PdfWriter writer, Document document) {
					try {
						PdfPTable footer = new PdfPTable(1);
						footer.setTotalWidth(document.getPageSize().getWidth() - 80);
						footer.setLockedWidth(true);

						Font footerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC,
								new BaseColor(128, 128, 128));
						PdfPCell cell = new PdfPCell(
								new Phrase("© 2025 KoroFood - Experiencias Gastronómicas Únicas | Página "
										+ writer.getPageNumber(), footerFont));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBorder(Rectangle.NO_BORDER);
						cell.setPaddingTop(10);
						footer.addCell(cell);

						footer.writeSelectedRows(0, -1, 40, 40, writer.getDirectContent());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			document.open();

			BaseColor primaryOrange = new BaseColor(230, 126, 34);
			BaseColor darkBrown = new BaseColor(62, 39, 35);
			BaseColor lightBeige = new BaseColor(250, 249, 246);
			BaseColor textGray = new BaseColor(107, 107, 107);

			PdfPTable headerTable = new PdfPTable(2);
			headerTable.setWidthPercentage(100);
			headerTable.setWidths(new float[] { 3, 2 });
			headerTable.setSpacingAfter(20);

			PdfPCell logoCell = new PdfPCell();
			logoCell.setBorder(Rectangle.NO_BORDER);
			logoCell.setPaddingBottom(10);

			Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 32, Font.BOLD, primaryOrange);
			Paragraph title = new Paragraph("KoroFood", titleFont);
			title.setAlignment(Element.ALIGN_LEFT);
			logoCell.addElement(title);

			Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, textGray);
			Paragraph subtitle = new Paragraph("Menú de Platos Especiales", subtitleFont);
			subtitle.setSpacingBefore(5);
			logoCell.addElement(subtitle);

			headerTable.addCell(logoCell);

			PdfPCell dateCell = new PdfPCell();
			dateCell.setBorder(Rectangle.NO_BORDER);
			dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			dateCell.setVerticalAlignment(Element.ALIGN_TOP);

			Font dateFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, textGray);
			String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			Paragraph datePara = new Paragraph("Fecha: " + currentDate, dateFont);
			datePara.setAlignment(Element.ALIGN_RIGHT);
			dateCell.addElement(datePara);

			Paragraph totalPlatos = new Paragraph("Total de platos: " + platos.size(), dateFont);
			totalPlatos.setAlignment(Element.ALIGN_RIGHT);
			totalPlatos.setSpacingBefore(3);
			dateCell.addElement(totalPlatos);

			headerTable.addCell(dateCell);
			document.add(headerTable);

			LineSeparator line = new LineSeparator();
			line.setLineColor(primaryOrange);
			line.setLineWidth(2);
			document.add(new Chunk(line));
			document.add(Chunk.NEWLINE);

			Font introFont = new Font(Font.FontFamily.HELVETICA, 11, Font.ITALIC, textGray);
			Paragraph intro = new Paragraph("Descubre nuestra exquisita selección de platos asiáticos, "
					+ "cuidadosamente preparados con ingredientes frescos y auténticos. "
					+ "Cada plato es una experiencia única que combina tradición y sabor.", introFont);
			intro.setAlignment(Element.ALIGN_JUSTIFIED);
			intro.setSpacingAfter(20);
			document.add(intro);

			Map<String, List<PlatoDtoFeign>> platosPorTipo = platos.stream()
					.sorted(Comparator.comparingInt(p -> ordenTipo(p.getTipoPlato()))).collect(Collectors
							.groupingBy(PlatoDtoFeign::getTipoPlato, LinkedHashMap::new, Collectors.toList()));

			for (Map.Entry<String, List<PlatoDtoFeign>> entry : platosPorTipo.entrySet()) {

				String tipoPlato = traducirTipoPlato(entry.getKey());
				List<PlatoDtoFeign> platosDelTipo = entry.getValue();

				Font categoryFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, darkBrown);
				Paragraph categoryTitle = new Paragraph(tipoPlato.toUpperCase(), categoryFont);
				categoryTitle.setSpacingBefore(15);
				categoryTitle.setSpacingAfter(10);

				PdfPTable categoryHeader = new PdfPTable(1);
				categoryHeader.setWidthPercentage(100);
				PdfPCell categoryCell = new PdfPCell(categoryTitle);
				categoryCell.setBackgroundColor(lightBeige);
				categoryCell.setBorder(Rectangle.NO_BORDER);
				categoryCell.setPadding(10);
				categoryCell.setBorderWidthLeft(4);
				categoryCell.setBorderColorLeft(primaryOrange);
				categoryHeader.addCell(categoryCell);

				document.add(categoryHeader);
				document.add(Chunk.NEWLINE);

				PdfPTable table = new PdfPTable(2);
				table.setWidthPercentage(100);
				table.setWidths(new float[] { 4f, 2f }); 
				table.setSpacingAfter(20);

				Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
				String[] headers = { "Nombre del Plato", "Imagen" };

				for (String header : headers) {
					PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
					headerCell.setBackgroundColor(primaryOrange);
					headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					headerCell.setPadding(10);
					headerCell.setBorder(Rectangle.NO_BORDER);
					table.addCell(headerCell);
				}

				// Datos de los platos
				Font nameFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, darkBrown);

				int contador = 0;
				for (PlatoDtoFeign plato : platosDelTipo) {
					BaseColor rowColor = (contador % 2 == 0) ? BaseColor.WHITE : new BaseColor(250, 250, 250);

					// Nombre
					PdfPCell nameCell = new PdfPCell(new Phrase(plato.getNombre(), nameFont));
					nameCell.setBackgroundColor(rowColor);
					nameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					nameCell.setPadding(12);
					nameCell.setBorder(Rectangle.NO_BORDER);
					table.addCell(nameCell);

					// Imagen
					PdfPCell imageCell = new PdfPCell();
					imageCell.setBackgroundColor(rowColor);
					imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					imageCell.setPadding(5);
					imageCell.setBorder(Rectangle.NO_BORDER);

					try {

						if (plato.getImagen() != null && !plato.getImagen().isEmpty()) {

							URL url = new URL(plato.getImagen());
							HttpURLConnection connection = (HttpURLConnection) url.openConnection();
							connection.setRequestProperty("User-Agent", "Mozilla/5.0");
							connection.connect();

							try (InputStream in = connection.getInputStream()) {

								BufferedImage original = ImageIO.read(in);

								if (original != null) {

									int min = Math.min(original.getWidth(), original.getHeight());
									int x = (original.getWidth() - min) / 2;
									int y = (original.getHeight() - min) / 2;

									BufferedImage square = original.getSubimage(x, y, min, min);

									BufferedImage resized = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
									Graphics2D g = resized.createGraphics();

									g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
											RenderingHints.VALUE_INTERPOLATION_BICUBIC);
									g.setRenderingHint(RenderingHints.KEY_RENDERING,
											RenderingHints.VALUE_RENDER_QUALITY);
									g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
											RenderingHints.VALUE_ANTIALIAS_ON);
									g.drawImage(square, 0, 0, 200, 200, null);

									g.dispose();

									ByteArrayOutputStream baosx = new ByteArrayOutputStream();
									ImageIO.write(resized, "png", baosx);
									byte[] bytes = baosx.toByteArray();

									Image img = Image.getInstance(bytes);
									img.scaleToFit(100f, 100f);
									img.setAlignment(Image.ALIGN_CENTER);

									img.setBorder(Rectangle.BOX);
									img.setBorderWidth(2);
									img.setBorderColor(primaryOrange);

									imageCell.addElement(img);

								} else {
									imageCell.addElement(new Phrase("Sin imagen"));
								}

							}

						} else {
							Font noImgFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);
							imageCell.addElement(new Phrase("Sin imagen", noImgFont));
						}
					} catch (Exception e) {
						Font errorFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.RED);
						imageCell.addElement(new Phrase("Error", errorFont));
					}

					table.addCell(imageCell);
					contador++;
				}
				document.add(table);
			}

			document.add(Chunk.NEWLINE);
			LineSeparator bottomLine = new LineSeparator();
			bottomLine.setLineColor(primaryOrange);
			bottomLine.setLineWidth(1);
			document.add(new Chunk(bottomLine));

			PdfPTable infoTable = new PdfPTable(2);
			infoTable.setWidthPercentage(100);
			infoTable.setSpacingBefore(15);

			Font infoTitleFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, darkBrown);
			Font infoDataFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, textGray);

			// Información de contacto
			PdfPCell contactCell = new PdfPCell();
			contactCell.setBorder(Rectangle.NO_BORDER);
			contactCell.addElement(new Phrase("Contacto", infoTitleFont));
			contactCell.addElement(new Phrase("Av. Larco 123, Miraflores, Lima", infoDataFont));
			contactCell.addElement(new Phrase("+51 987 654 321", infoDataFont));
			contactCell.addElement(new Phrase("hola@korofood.pe", infoDataFont));
			infoTable.addCell(contactCell);

			// Horarios
			PdfPCell horariosCell = new PdfPCell();
			horariosCell.setBorder(Rectangle.NO_BORDER);
			horariosCell.addElement(new Phrase("Horarios de Atención", infoTitleFont));
			horariosCell.addElement(new Phrase("Lun - Jue: 12:00 - 22:00", infoDataFont));
			horariosCell.addElement(new Phrase("Vie - Sáb: 12:00 - 24:00", infoDataFont));
			horariosCell.addElement(new Phrase("Dom: 12:00 - 21:00", infoDataFont));
			infoTable.addCell(horariosCell);

			document.add(infoTable);

		} finally {
			document.close();
		}

		return baos.toByteArray();
	}

	private String traducirTipoPlato(String tipo) {
		switch (tipo) {
		case "E":
			return "Entrada";
		case "S":
			return "Segundo";
		case "P":
			return "Postre";
		case "B":
			return "Bebida";
		default:
			return tipo;
		}
	}

	private int ordenTipo(String tipo) {
		switch (tipo) {
		case "E":
			return 1;
		case "S":
			return 2;
		case "P":
			return 3;
		case "B":
			return 4;
		default:
			return 99;
		}
	}

}
