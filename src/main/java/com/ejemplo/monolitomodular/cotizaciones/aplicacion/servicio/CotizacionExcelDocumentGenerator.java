package com.ejemplo.monolitomodular.cotizaciones.aplicacion.servicio;

import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoComidaRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoEventoRepository;
import com.ejemplo.monolitomodular.clientes.dominio.modelo.Cliente;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.Cotizacion;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.CotizacionItem;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import com.ejemplo.monolitomodular.salones.dominio.modelo.Salon;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class CotizacionExcelDocumentGenerator {

    private static final String TEMPLATE_PATH = "templates/cotizaciones/excel/";

    private final TipoEventoRepository tipoEventoRepository;
    private final TipoComidaRepository tipoComidaRepository;
    private final ExcelTemplates templates;

    public CotizacionExcelDocumentGenerator(
            TipoEventoRepository tipoEventoRepository,
            TipoComidaRepository tipoComidaRepository
    ) {
        this.tipoEventoRepository = tipoEventoRepository;
        this.tipoComidaRepository = tipoComidaRepository;
        this.templates = ExcelTemplates.load();
    }

    public byte[] generar(Cotizacion cotizacion, Evento evento, ReservaSalon reserva, Cliente cliente, Salon salon) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream(); ZipOutputStream zip = new ZipOutputStream(output)) {
            write(zip, "[Content_Types].xml", templates.contentTypes());
            write(zip, "_rels/.rels", templates.rootRels());
            write(zip, "docProps/core.xml", coreProps(cotizacion));
            write(zip, "docProps/app.xml", templates.appProps());
            write(zip, "xl/workbook.xml", templates.workbook());
            write(zip, "xl/_rels/workbook.xml.rels", templates.workbookRels());
            write(zip, "xl/styles.xml", templates.styles());
            write(zip, "xl/worksheets/sheet1.xml", worksheet(cotizacion, evento, reserva, cliente, salon));
            zip.finish();
            return output.toByteArray();
        } catch (IOException ex) {
            throw new DomainException("No se pudo generar el documento de cotizacion");
        }
    }

    private String worksheet(Cotizacion cotizacion, Evento evento, ReservaSalon reserva, Cliente cliente, Salon salon) {
        StringBuilder rows = new StringBuilder();
        String tipoEvento = nombreTipoEvento(evento);
        String tipoComida = tipoComidaRepository.buscarPorId(evento.getTipoComidaId())
                .map(tipo -> tipo.getNombre())
                .orElse(evento.getTipoComidaId().toString());

        addEncabezado(rows, cotizacion);
        addDatosCliente(rows, cliente);
        addDatosEvento(rows, evento, reserva, salon, tipoEvento, tipoComida);
        addResumenFinanciero(rows, cotizacion);

        DetalleEconomicoRows detalleRows = addDetalleEconomico(rows, cotizacion);
        addObservaciones(rows, detalleRows.noteRow(), cotizacion);

        return templates.worksheet().formatted(rows, detalleRows.lastItemRow(), detalleRows.noteRow(), detalleRows.noteRow());
    }

    private void addEncabezado(StringBuilder rows, Cotizacion cotizacion) {
        addRow(rows, 1, cell("A1", "CLUB BOYACA", 1));
        addRow(rows, 2, cell("A2", "Cotizacion formal de evento", 2));
        addRow(rows, 4,
                cell("A4", "Cotizacion No.", 4),
                cell("B4", cotizacion.getId().toString(), 5),
                cell("E4", "Estado", 4),
                cell("F4", cotizacion.getEstado().name(), 5));
        addRow(rows, 5,
                cell("A5", "Fecha de emision", 4),
                cell("B5", formatoFecha(LocalDateTime.now()), 5),
                cell("E5", "Vigente", 4),
                cell("F5", cotizacion.isVigente() ? "Si" : "No", 5));
    }

    private void addDatosCliente(StringBuilder rows, Cliente cliente) {
        addSection(rows, 7, "Datos del cliente");
        addRow(rows, 8, cell("A8", "Cliente", 4), cell("B8", cliente.getNombreCompleto(), 5),
                cell("E8", "Tipo cliente", 4), cell("F8", cliente.getTipoCliente().name(), 5));
        addRow(rows, 9, cell("A9", "Cedula", 4), cell("B9", cliente.getCedula(), 5),
                cell("E9", "Telefono", 4), cell("F9", cliente.getTelefono(), 5));
        addRow(rows, 10, cell("A10", "Correo", 4), cell("B10", cliente.getCorreo(), 5));
    }

    private void addDatosEvento(
            StringBuilder rows,
            Evento evento,
            ReservaSalon reserva,
            Salon salon,
            String tipoEvento,
            String tipoComida
    ) {
        addSection(rows, 12, "Datos del evento y reserva");
        addRow(rows, 13, cell("A13", "Tipo de evento", 4), cell("B13", tipoEvento, 5),
                cell("E13", "Tipo de comida", 4), cell("F13", tipoComida, 5));
        addRow(rows, 14, cell("A14", "Salon", 4), cell("B14", salon.getNombre(), 5),
                cell("E14", "Capacidad salon", 4), numberCell("F14", salon.getCapacidad(), 6));
        addRow(rows, 15, cell("A15", "Invitados", 4), numberCell("B15", reserva.getNumInvitados(), 6),
                cell("E15", "Version reserva", 4), numberCell("F15", reserva.getVersion(), 6));
        addRow(rows, 16, cell("A16", "Inicio", 4), cell("B16", formatoFecha(evento.getFechaHoraInicio()), 5),
                cell("E16", "Fin", 4), cell("F16", formatoFecha(evento.getFechaHoraFin()), 5));
    }

    private void addResumenFinanciero(StringBuilder rows, Cotizacion cotizacion) {
        addSection(rows, 18, "Resumen financiero");
        addRow(rows, 19,
                cell("A19", "Subtotal calculado", 4),
                moneyCell("B19", cotizacion.getValorSubtotal(), 7),
                cell("D19", "Descuento", 4),
                moneyCell("E19", cotizacion.getDescuento(), 7),
                cell("G19", "Total cotizacion", 4),
                moneyCell("H19", cotizacion.getValorTotal(), 7));
        addRow(rows, 20,
                cell("A20", "Total items", 4),
                numberCell("B20", cotizacion.getItems().size(), 6),
                cell("D20", "Valores", 4),
                cell("E20", "Pesos colombianos (COP)", 5),
                cell("G20", "Emitida", 4),
                cell("H20", formatoFecha(LocalDateTime.now()), 5));
    }

    private DetalleEconomicoRows addDetalleEconomico(StringBuilder rows, Cotizacion cotizacion) {
        addSection(rows, 22, "Detalle economico");
        addRow(rows, 23,
                cell("A23", "No.", 8),
                cell("B23", "Origen", 8),
                cell("C23", "Concepto", 8),
                cell("D23", "Cobro", 8),
                cell("E23", "Descripcion", 8),
                cell("F23", "Precio base", 8),
                cell("G23", "Precio aplicado", 8),
                cell("H23", "Cantidad", 8),
                cell("I23", "Subtotal", 8));

        int row = 24;
        int index = 1;
        for (CotizacionItem item : cotizacion.getItems()) {
            BigDecimal precioAplicado = item.getPrecioOverride() == null ? item.getPrecioBase() : item.getPrecioOverride();
            addRow(rows, row,
                    numberCell("A" + row, index, 6),
                    cell("B" + row, origenItem(item), 5),
                    cell("C" + row, item.getTipoConcepto(), 5),
                    cell("D" + row, modalidadCobro(item), 5),
                    cell("E" + row, item.getDescripcion(), 5),
                    moneyCell("F" + row, item.getPrecioBase(), 7),
                    moneyCell("G" + row, precioAplicado, 7),
                    numberCell("H" + row, item.getCantidad(), 6),
                    formulaCell("I" + row, "G" + row + "*H" + row, 7));
            row++;
            index++;
        }

        int subtotalRow = row + 1;
        int descuentoRow = row + 2;
        int totalRow = row + 3;
        int firstItemRow = 24;
        int lastItemRow = Math.max(firstItemRow, row - 1);

        addRow(rows, subtotalRow, cell("H" + subtotalRow, "Subtotal", 9),
                formulaCell("I" + subtotalRow, "SUM(I" + firstItemRow + ":I" + lastItemRow + ")", 10));
        addRow(rows, descuentoRow, cell("H" + descuentoRow, "Descuento", 9),
                moneyCell("I" + descuentoRow, cotizacion.getDescuento(), 10));
        addRow(rows, totalRow, cell("H" + totalRow, "Total", 11),
                formulaCell("I" + totalRow, "I" + subtotalRow + "-I" + descuentoRow, 12));

        return new DetalleEconomicoRows(lastItemRow, row + 5);
    }

    private void addObservaciones(StringBuilder rows, int noteRow, Cotizacion cotizacion) {
        addSection(rows, noteRow, "Observaciones");
        addRow(rows, noteRow + 1, cell("A" + (noteRow + 1), cotizacion.getObservaciones() == null
                ? "Sin observaciones registradas."
                : cotizacion.getObservaciones(), 13));
        addRow(rows, noteRow + 3, cell("A" + (noteRow + 3),
                "Documento generado automaticamente por SGIE Club Boyaca. Valores expresados en pesos colombianos.", 14));
    }

    private String nombreTipoEvento(Evento evento) {
        return tipoEventoRepository.buscarPorId(evento.getTipoEventoId())
                .map(tipo -> tipo.getNombre())
                .orElse(evento.getTipoEventoId().toString());
    }

    private String origenItem(CotizacionItem item) {
        String tipo = item.getTipoConcepto() == null ? "" : item.getTipoConcepto().toUpperCase(Locale.ROOT);
        if (tipo.contains("SALON") || tipo.contains("ALQUILER")) {
            return "Salon";
        }
        if (tipo.contains("MENU") || tipo.contains("PLATO")) {
            return "Menu";
        }
        return "Montaje";
    }

    private String modalidadCobro(CotizacionItem item) {
        return item.getCantidad() == 1 ? "Servicio" : "Unidad";
    }

    private void addSection(StringBuilder rows, int row, String title) {
        addRow(rows, row, cell("A" + row, title, 3));
    }

    private void addRow(StringBuilder rows, int rowNumber, String... cells) {
        rows.append("    <row r=\"").append(rowNumber).append("\">");
        for (String cell : cells) {
            rows.append(cell);
        }
        rows.append("</row>\n");
    }

    private String cell(String ref, String value, int style) {
        return "<c r=\"" + ref + "\" s=\"" + style + "\" t=\"inlineStr\"><is><t>" + xml(spreadsheetText(value)) + "</t></is></c>";
    }

    private String numberCell(String ref, Number value, int style) {
        return "<c r=\"" + ref + "\" s=\"" + style + "\"><v>" + value + "</v></c>";
    }

    private String moneyCell(String ref, BigDecimal value, int style) {
        return "<c r=\"" + ref + "\" s=\"" + style + "\"><v>" + excelNumber(value) + "</v></c>";
    }

    private String formulaCell(String ref, String formula, int style) {
        return "<c r=\"" + ref + "\" s=\"" + style + "\"><f>" + xml(formula) + "</f></c>";
    }

    private String excelNumber(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value)
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
    }

    private String formatoFecha(LocalDateTime fecha) {
        return fecha == null ? "" : fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private void write(ZipOutputStream zip, String path, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(path));
        zip.write(content.stripLeading().getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private String coreProps(Cotizacion cotizacion) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return templates.coreProps().formatted(xml(cotizacion.getId().toString()), now, now);
    }

    private String xml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "")
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String spreadsheetText(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        String trimmed = value.stripLeading();
        if (trimmed.startsWith("=") || trimmed.startsWith("+") || trimmed.startsWith("-") || trimmed.startsWith("@")) {
            return "'" + value;
        }
        return value;
    }

    private record ExcelTemplates(
            String contentTypes,
            String rootRels,
            String workbookRels,
            String workbook,
            String coreProps,
            String appProps,
            String styles,
            String worksheet
    ) {

        private static ExcelTemplates load() {
            return new ExcelTemplates(
                    readTemplate("content-types.xml"),
                    readTemplate("root-rels.xml"),
                    readTemplate("workbook-rels.xml"),
                    readTemplate("workbook.xml"),
                    readTemplate("core-props.xml"),
                    readTemplate("app-props.xml"),
                    readTemplate("styles.xml"),
                    readTemplate("worksheet.xml")
            );
        }

        private static String readTemplate(String name) {
            ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH + name);
            try (InputStream input = resource.getInputStream()) {
                return new String(input.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                throw new IllegalStateException("No se pudo cargar la plantilla Excel " + name, ex);
            }
        }
    }

    private record DetalleEconomicoRows(int lastItemRow, int noteRow) {
    }
}
