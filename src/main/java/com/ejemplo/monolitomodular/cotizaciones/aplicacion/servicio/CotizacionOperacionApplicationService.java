package com.ejemplo.monolitomodular.cotizaciones.aplicacion.servicio;

import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoComidaRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoEventoRepository;
import com.ejemplo.monolitomodular.clientes.dominio.modelo.Cliente;
import com.ejemplo.monolitomodular.clientes.dominio.puerto.salida.ClienteRepository;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.CotizacionItemView;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.CotizacionView;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.DocumentoCotizacionView;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.DescargarDocumentoCotizacionUseCase;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.EnviarCotizacionEmailUseCase;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.EnviarCotizacionUseCase;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.ListarCotizacionesEventoUseCase;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.Cotizacion;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.CotizacionItem;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.EstadoCotizacion;
import com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida.CotizacionRepository;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.EventoRepository;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.ReservaSalonRepository;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.CrearNotificacionCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.CrearNotificacionUseCase;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;
import com.ejemplo.monolitomodular.salones.dominio.modelo.Salon;
import com.ejemplo.monolitomodular.salones.dominio.puerto.salida.SalonRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class CotizacionOperacionApplicationService implements
        ListarCotizacionesEventoUseCase,
        DescargarDocumentoCotizacionUseCase,
        EnviarCotizacionEmailUseCase {

    private final CotizacionRepository cotizacionRepository;
    private final ReservaSalonRepository reservaSalonRepository;
    private final EventoRepository eventoRepository;
    private final ClienteRepository clienteRepository;
    private final SalonRepository salonRepository;
    private final TipoEventoRepository tipoEventoRepository;
    private final TipoComidaRepository tipoComidaRepository;
    private final CrearNotificacionUseCase crearNotificacionUseCase;
    private final EnviarCotizacionUseCase enviarCotizacionUseCase;
    private final ObjectMapper objectMapper;

    public CotizacionOperacionApplicationService(
            CotizacionRepository cotizacionRepository,
            ReservaSalonRepository reservaSalonRepository,
            EventoRepository eventoRepository,
            ClienteRepository clienteRepository,
            SalonRepository salonRepository,
            TipoEventoRepository tipoEventoRepository,
            TipoComidaRepository tipoComidaRepository,
            CrearNotificacionUseCase crearNotificacionUseCase,
            EnviarCotizacionUseCase enviarCotizacionUseCase,
            ObjectMapper objectMapper
    ) {
        this.cotizacionRepository = cotizacionRepository;
        this.reservaSalonRepository = reservaSalonRepository;
        this.eventoRepository = eventoRepository;
        this.clienteRepository = clienteRepository;
        this.salonRepository = salonRepository;
        this.tipoEventoRepository = tipoEventoRepository;
        this.tipoComidaRepository = tipoComidaRepository;
        this.crearNotificacionUseCase = crearNotificacionUseCase;
        this.enviarCotizacionUseCase = enviarCotizacionUseCase;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<CotizacionView> listarPorEvento(UUID eventoId) {
        eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new DomainException("Evento no encontrado"));
        return cotizacionRepository.listarPorEventoId(eventoId).stream()
                .map(this::toView)
                .toList();
    }

    @Override
    public DocumentoCotizacionView descargar(UUID cotizacionId) {
        DocumentoContext context = documentoContext(cotizacionId);
        return new DocumentoCotizacionView(
                "cotizacion-" + context.cotizacion().getId() + ".xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                generarExcel(context.cotizacion(), context.evento(), context.reserva(), context.cliente(), context.salon())
        );
    }

    @Override
    public DocumentoCotizacionView descargarPdf(UUID cotizacionId) {
        DocumentoContext context = documentoContext(cotizacionId);
        return new DocumentoCotizacionView(
                "cotizacion-" + context.cotizacion().getId() + ".pdf",
                "application/pdf",
                generarPdf(context.cotizacion(), context.evento(), context.reserva(), context.cliente(), context.salon())
        );
    }

    private DocumentoContext documentoContext(UUID cotizacionId) {
        Cotizacion cotizacion = buscarCotizacion(cotizacionId);
        ReservaSalon reserva = reserva(cotizacion);
        Evento evento = evento(reserva);
        Cliente cliente = clienteRepository.buscarPorId(evento.getClienteId())
                .orElseThrow(() -> new DomainException("Cliente no encontrado"));
        Salon salon = salonRepository.buscarPorId(reserva.getSalonId())
                .orElseThrow(() -> new DomainException("Salon no encontrado"));
        return new DocumentoContext(cotizacion, reserva, evento, cliente, salon);
    }

    @Override
    @Transactional
    public CotizacionView enviarPorEmail(UUID cotizacionId) {
        Cotizacion cotizacion = buscarCotizacion(cotizacionId);
        Evento evento = evento(cotizacion);
        evento.validarOperable();
        CotizacionView view = switch (cotizacion.getEstado()) {
            case GENERADA -> enviarCotizacionUseCase.enviar(cotizacionId);
            case ENVIADA, ACEPTADA -> toView(cotizacion);
            default -> throw new DomainException("Solo una cotizacion generada, enviada o aceptada puede enviarse por email");
        };
        Cliente cliente = clienteRepository.buscarPorId(evento.getClienteId())
                .orElseThrow(() -> new DomainException("Cliente no encontrado"));
        crearNotificacionUseCase.ejecutar(new CrearNotificacionCommand(
                evento.getId(),
                TipoNotificacion.COTIZACION_CLIENTE,
                LocalDateTime.now(),
                payloadCotizacion(view, cliente, evento),
                List.of(new CrearNotificacionCommand.Destinatario(null, cliente.getTelefono(), cliente.getCorreo()))
        ));
        return view;
    }

    private Cotizacion buscarCotizacion(UUID cotizacionId) {
        return cotizacionRepository.buscarPorId(cotizacionId)
                .orElseThrow(() -> new DomainException("Cotizacion no encontrada"));
    }

    private Evento evento(Cotizacion cotizacion) {
        return evento(reserva(cotizacion));
    }

    private ReservaSalon reserva(Cotizacion cotizacion) {
        return reservaSalonRepository.buscarPorId(cotizacion.getReservaId())
                .orElseThrow(() -> new DomainException("Reserva asociada a la cotizacion no encontrada"));
    }

    private Evento evento(ReservaSalon reserva) {
        return eventoRepository.buscarPorId(reserva.getEventoId())
                .orElseThrow(() -> new DomainException("Evento asociado a la cotizacion no encontrado"));
    }

    private String payloadCotizacion(CotizacionView cotizacion, Cliente cliente, Evento evento) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "tipo", "COTIZACION",
                    "cliente", cliente.getNombreCompleto(),
                    "cotizacionId", cotizacion.id(),
                    "fechaEvento", evento.getFechaHoraInicio().toString(),
                    "valorTotal", cotizacion.valorTotal()
            ));
        } catch (Exception ex) {
            return "{}";
        }
    }

    private byte[] generarExcel(Cotizacion cotizacion, Evento evento, ReservaSalon reserva, Cliente cliente, Salon salon) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream(); ZipOutputStream zip = new ZipOutputStream(output)) {
            write(zip, "[Content_Types].xml", contentTypes());
            write(zip, "_rels/.rels", rootRels());
            write(zip, "docProps/core.xml", coreProps(cotizacion));
            write(zip, "docProps/app.xml", appProps());
            write(zip, "xl/workbook.xml", workbook());
            write(zip, "xl/_rels/workbook.xml.rels", workbookRels());
            write(zip, "xl/styles.xml", styles());
            write(zip, "xl/worksheets/sheet1.xml", worksheet(cotizacion, evento, reserva, cliente, salon));
            zip.finish();
            return output.toByteArray();
        } catch (IOException ex) {
            throw new DomainException("No se pudo generar el documento de cotizacion");
        }
    }

    private byte[] generarPdf(Cotizacion cotizacion, Evento evento, ReservaSalon reserva, Cliente cliente, Salon salon) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PdfWriter pdf = new PdfWriter(document);
            pdf.title("CLUB BOYACA");
            pdf.subtitle("Cotizacion formal de evento");
            pdf.text("Cotizacion No. " + cotizacion.getId());
            pdf.text("Fecha de emision: " + formatoFecha(LocalDateTime.now()) + "    Estado: " + cotizacion.getEstado().name());
            pdf.space(10);

            pdf.section("Datos del cliente");
            pdf.text("Cliente: " + cliente.getNombreCompleto());
            pdf.text("Cedula: " + cliente.getCedula() + "    Telefono: " + cliente.getTelefono());
            pdf.text("Correo: " + cliente.getCorreo());
            pdf.space(8);

            pdf.section("Datos del evento");
            pdf.text("Tipo de evento: " + nombreTipoEvento(evento));
            pdf.text("Salon: " + salon.getNombre() + "    Invitados: " + reserva.getNumInvitados());
            pdf.text("Inicio: " + formatoFecha(evento.getFechaHoraInicio()) + "    Fin: " + formatoFecha(evento.getFechaHoraFin()));
            pdf.space(8);

            pdf.section("Resumen financiero");
            pdf.text("Subtotal: " + money(cotizacion.getValorSubtotal()));
            pdf.text("Descuento: " + money(cotizacion.getDescuento()));
            pdf.emphasis("Total cotizacion: " + money(cotizacion.getValorTotal()));
            pdf.space(8);

            pdf.section("Detalle economico");
            pdf.tableHeader();
            for (CotizacionItem item : cotizacion.getItems()) {
                BigDecimal precioAplicado = item.getPrecioOverride() == null ? item.getPrecioBase() : item.getPrecioOverride();
                pdf.tableRow(
                        origenItem(item),
                        item.getDescripcion(),
                        Integer.toString(item.getCantidad()),
                        money(precioAplicado),
                        money(precioAplicado.multiply(BigDecimal.valueOf(item.getCantidad())))
                );
            }

            pdf.space(10);
            pdf.section("Observaciones");
            pdf.paragraph(cotizacion.getObservaciones() == null || cotizacion.getObservaciones().isBlank()
                    ? "Sin observaciones registradas."
                    : cotizacion.getObservaciones());
            pdf.footer();
            pdf.close();
            document.save(output);
            return output.toByteArray();
        } catch (IOException ex) {
            throw new DomainException("No se pudo generar el PDF de cotizacion");
        }
    }

    private String worksheet(Cotizacion cotizacion, Evento evento, ReservaSalon reserva, Cliente cliente, Salon salon) {
        StringBuilder rows = new StringBuilder();
        String tipoEvento = nombreTipoEvento(evento);
        String tipoComida = tipoComidaRepository.buscarPorId(evento.getTipoComidaId())
                .map(tipo -> tipo.getNombre())
                .orElse(evento.getTipoComidaId().toString());

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

        addSection(rows, 7, "Datos del cliente");
        addRow(rows, 8, cell("A8", "Cliente", 4), cell("B8", cliente.getNombreCompleto(), 5),
                cell("E8", "Tipo cliente", 4), cell("F8", cliente.getTipoCliente().name(), 5));
        addRow(rows, 9, cell("A9", "Cedula", 4), cell("B9", cliente.getCedula(), 5),
                cell("E9", "Telefono", 4), cell("F9", cliente.getTelefono(), 5));
        addRow(rows, 10, cell("A10", "Correo", 4), cell("B10", cliente.getCorreo(), 5));

        addSection(rows, 12, "Datos del evento y reserva");
        addRow(rows, 13, cell("A13", "Tipo de evento", 4), cell("B13", tipoEvento, 5),
                cell("E13", "Tipo de comida", 4), cell("F13", tipoComida, 5));
        addRow(rows, 14, cell("A14", "Salon", 4), cell("B14", salon.getNombre(), 5),
                cell("E14", "Capacidad salon", 4), numberCell("F14", salon.getCapacidad(), 6));
        addRow(rows, 15, cell("A15", "Invitados", 4), numberCell("B15", reserva.getNumInvitados(), 6),
                cell("E15", "Version reserva", 4), numberCell("F15", reserva.getVersion(), 6));
        addRow(rows, 16, cell("A16", "Inicio", 4), cell("B16", formatoFecha(evento.getFechaHoraInicio()), 5),
                cell("E16", "Fin", 4), cell("F16", formatoFecha(evento.getFechaHoraFin()), 5));

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
        int noteRow = row + 5;
        int firstItemRow = 24;
        int lastItemRow = Math.max(firstItemRow, row - 1);

        addRow(rows, subtotalRow, cell("H" + subtotalRow, "Subtotal", 9),
                formulaCell("I" + subtotalRow, "SUM(I" + firstItemRow + ":I" + lastItemRow + ")", 10));
        addRow(rows, descuentoRow, cell("H" + descuentoRow, "Descuento", 9),
                moneyCell("I" + descuentoRow, cotizacion.getDescuento(), 10));
        addRow(rows, totalRow, cell("H" + totalRow, "Total", 11),
                formulaCell("I" + totalRow, "I" + subtotalRow + "-I" + descuentoRow, 12));

        addSection(rows, noteRow, "Observaciones");
        addRow(rows, noteRow + 1, cell("A" + (noteRow + 1), cotizacion.getObservaciones() == null
                ? "Sin observaciones registradas."
                : cotizacion.getObservaciones(), 13));
        addRow(rows, noteRow + 3, cell("A" + (noteRow + 3),
                "Documento generado automaticamente por SGIE Club Boyaca. Valores expresados en pesos colombianos.", 14));

        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
                  <sheetViews><sheetView showGridLines="0" workbookViewId="0"><pane ySplit="23" topLeftCell="A24" activePane="bottomLeft" state="frozen"/></sheetView></sheetViews>
                  <cols>
                    <col min="1" max="1" width="8" customWidth="1"/>
                    <col min="2" max="2" width="14" customWidth="1"/>
                    <col min="3" max="3" width="20" customWidth="1"/>
                    <col min="4" max="4" width="16" customWidth="1"/>
                    <col min="5" max="5" width="42" customWidth="1"/>
                    <col min="6" max="6" width="16" customWidth="1"/>
                    <col min="7" max="7" width="18" customWidth="1"/>
                    <col min="8" max="8" width="13" customWidth="1"/>
                    <col min="9" max="9" width="18" customWidth="1"/>
                  </cols>
                  <sheetData>
                %s
                  </sheetData>
                  <autoFilter ref="A23:I%d"/>
                  <mergeCells count="7">
                    <mergeCell ref="A1:I1"/>
                    <mergeCell ref="A2:I2"/>
                    <mergeCell ref="A7:I7"/>
                    <mergeCell ref="A12:I12"/>
                    <mergeCell ref="A18:I18"/>
                    <mergeCell ref="A22:I22"/>
                    <mergeCell ref="A%d:I%d"/>
                  </mergeCells>
                  <pageMargins left="0.5" right="0.5" top="0.7" bottom="0.7" header="0.3" footer="0.3"/>
                </worksheet>
                """.formatted(rows, lastItemRow, noteRow, noteRow);
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

    private String money(BigDecimal value) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        format.setMaximumFractionDigits(0);
        return format.format(value == null ? BigDecimal.ZERO : value);
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
        return "<c r=\"" + ref + "\" s=\"" + style + "\" t=\"inlineStr\"><is><t>" + xml(value) + "</t></is></c>";
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
        zip.write(content.stripLeading().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private String xml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String contentTypes() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                  <Default Extension="xml" ContentType="application/xml"/>
                  <Override PartName="/docProps/core.xml" ContentType="application/vnd.openxmlformats-package.core-properties+xml"/>
                  <Override PartName="/docProps/app.xml" ContentType="application/vnd.openxmlformats-officedocument.extended-properties+xml"/>
                  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
                  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
                  <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
                </Types>
                """;
    }

    private String rootRels() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
                  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties" Target="docProps/core.xml"/>
                  <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties" Target="docProps/app.xml"/>
                </Relationships>
                """;
    }

    private String workbookRels() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
                  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
                </Relationships>
                """;
    }

    private String workbook() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
                  <sheets>
                    <sheet name="Cotizacion" sheetId="1" r:id="rId1"/>
                  </sheets>
                  <calcPr calcMode="auto" fullCalcOnLoad="1"/>
                </workbook>
                """;
    }

    private String coreProps(Cotizacion cotizacion) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dcmitype="http://purl.org/dc/dcmitype/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                  <dc:title>Cotizacion %s</dc:title>
                  <dc:creator>SGIE Club Boyaca</dc:creator>
                  <cp:lastModifiedBy>SGIE Club Boyaca</cp:lastModifiedBy>
                  <dcterms:created xsi:type="dcterms:W3CDTF">%s</dcterms:created>
                  <dcterms:modified xsi:type="dcterms:W3CDTF">%s</dcterms:modified>
                </cp:coreProperties>
                """.formatted(xml(cotizacion.getId().toString()), now, now);
    }

    private String appProps() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/extended-properties" xmlns:vt="http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes">
                  <Application>SGIE Club Boyaca</Application>
                  <DocSecurity>0</DocSecurity>
                  <ScaleCrop>false</ScaleCrop>
                  <HeadingPairs><vt:vector size="2" baseType="variant"><vt:variant><vt:lpstr>Worksheets</vt:lpstr></vt:variant><vt:variant><vt:i4>1</vt:i4></vt:variant></vt:vector></HeadingPairs>
                  <TitlesOfParts><vt:vector size="1" baseType="lpstr"><vt:lpstr>Cotizacion</vt:lpstr></vt:vector></TitlesOfParts>
                </Properties>
                """;
    }

    private String styles() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
                  <numFmts count="1"><numFmt numFmtId="164" formatCode="$ #,##0.00"/></numFmts>
                  <fonts count="6">
                    <font><sz val="11"/><color rgb="FF1F2937"/><name val="Aptos"/></font>
                    <font><b/><sz val="20"/><color rgb="FFFFFFFF"/><name val="Aptos Display"/></font>
                    <font><b/><sz val="12"/><color rgb="FFFFFFFF"/><name val="Aptos"/></font>
                    <font><b/><sz val="10"/><color rgb="FF3F3524"/><name val="Aptos"/></font>
                    <font><b/><sz val="11"/><color rgb="FFFFFFFF"/><name val="Aptos"/></font>
                    <font><i/><sz val="10"/><color rgb="FF6B7280"/><name val="Aptos"/></font>
                  </fonts>
                  <fills count="7">
                    <fill><patternFill patternType="none"/></fill>
                    <fill><patternFill patternType="gray125"/></fill>
                    <fill><patternFill patternType="solid"><fgColor rgb="FF1F1A14"/><bgColor indexed="64"/></patternFill></fill>
                    <fill><patternFill patternType="solid"><fgColor rgb="FFA8841C"/><bgColor indexed="64"/></patternFill></fill>
                    <fill><patternFill patternType="solid"><fgColor rgb="FFF5EFE1"/><bgColor indexed="64"/></patternFill></fill>
                    <fill><patternFill patternType="solid"><fgColor rgb="FFE8D38D"/><bgColor indexed="64"/></patternFill></fill>
                    <fill><patternFill patternType="solid"><fgColor rgb="FFF9FAFB"/><bgColor indexed="64"/></patternFill></fill>
                  </fills>
                  <borders count="3">
                    <border><left/><right/><top/><bottom/><diagonal/></border>
                    <border><left style="thin"><color rgb="FFE5E7EB"/></left><right style="thin"><color rgb="FFE5E7EB"/></right><top style="thin"><color rgb="FFE5E7EB"/></top><bottom style="thin"><color rgb="FFE5E7EB"/></bottom><diagonal/></border>
                    <border><top style="thin"><color rgb="FFA8841C"/></top><bottom style="medium"><color rgb="FFA8841C"/></bottom><diagonal/></border>
                  </borders>
                  <cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs>
                  <cellXfs count="15">
                    <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>
                    <xf numFmtId="0" fontId="1" fillId="2" borderId="0" xfId="0" applyFont="1" applyFill="1"><alignment horizontal="center"/></xf>
                    <xf numFmtId="0" fontId="2" fillId="2" borderId="0" xfId="0" applyFont="1" applyFill="1"><alignment horizontal="center"/></xf>
                    <xf numFmtId="0" fontId="2" fillId="3" borderId="0" xfId="0" applyFont="1" applyFill="1"><alignment horizontal="left"/></xf>
                    <xf numFmtId="0" fontId="3" fillId="4" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1"/>
                    <xf numFmtId="0" fontId="0" fillId="6" borderId="1" xfId="0" applyFill="1" applyBorder="1"><alignment wrapText="1"/></xf>
                    <xf numFmtId="0" fontId="0" fillId="6" borderId="1" xfId="0" applyFill="1" applyBorder="1"><alignment horizontal="center"/></xf>
                    <xf numFmtId="164" fontId="0" fillId="6" borderId="1" xfId="0" applyNumberFormat="1" applyFill="1" applyBorder="1"/>
                    <xf numFmtId="0" fontId="4" fillId="2" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1"><alignment horizontal="center"/></xf>
                    <xf numFmtId="0" fontId="3" fillId="5" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1"/>
                    <xf numFmtId="164" fontId="3" fillId="5" borderId="1" xfId="0" applyNumberFormat="1" applyFont="1" applyFill="1" applyBorder="1"/>
                    <xf numFmtId="0" fontId="4" fillId="3" borderId="2" xfId="0" applyFont="1" applyFill="1" applyBorder="1"/>
                    <xf numFmtId="164" fontId="4" fillId="3" borderId="2" xfId="0" applyNumberFormat="1" applyFont="1" applyFill="1" applyBorder="1"/>
                    <xf numFmtId="0" fontId="0" fillId="6" borderId="1" xfId="0" applyFill="1" applyBorder="1"><alignment wrapText="1" vertical="top"/></xf>
                    <xf numFmtId="0" fontId="5" fillId="0" borderId="0" xfId="0" applyFont="1"/>
                  </cellXfs>
                  <cellStyles count="1"><cellStyle name="Normal" xfId="0" builtinId="0"/></cellStyles>
                </styleSheet>
                """;
    }

    private record DocumentoContext(
            Cotizacion cotizacion,
            ReservaSalon reserva,
            Evento evento,
            Cliente cliente,
            Salon salon
    ) {
    }

    private static class PdfWriter implements AutoCloseable {

        private static final float MARGIN = 48;
        private final PDDocument document;
        private PDPage page;
        private PDPageContentStream content;
        private float y;

        private PdfWriter(PDDocument document) throws IOException {
            this.document = document;
            newPage();
        }

        private void title(String text) throws IOException {
            write(text, PDType1Font.HELVETICA_BOLD, 18, MARGIN, y);
            y -= 24;
        }

        private void subtitle(String text) throws IOException {
            write(text, PDType1Font.HELVETICA_BOLD, 13, MARGIN, y);
            y -= 22;
        }

        private void section(String text) throws IOException {
            ensureSpace(32);
            write(text, PDType1Font.HELVETICA_BOLD, 11, MARGIN, y);
            y -= 16;
        }

        private void text(String text) throws IOException {
            ensureSpace(16);
            write(text, PDType1Font.HELVETICA, 9, MARGIN, y);
            y -= 14;
        }

        private void emphasis(String text) throws IOException {
            ensureSpace(18);
            write(text, PDType1Font.HELVETICA_BOLD, 10, MARGIN, y);
            y -= 16;
        }

        private void paragraph(String text) throws IOException {
            String remaining = text == null ? "" : text;
            while (remaining.length() > 95) {
                text(remaining.substring(0, 95));
                remaining = remaining.substring(95);
            }
            text(remaining);
        }

        private void tableHeader() throws IOException {
            ensureSpace(22);
            write("Origen", PDType1Font.HELVETICA_BOLD, 8, MARGIN, y);
            write("Descripcion", PDType1Font.HELVETICA_BOLD, 8, MARGIN + 70, y);
            write("Cant.", PDType1Font.HELVETICA_BOLD, 8, MARGIN + 330, y);
            write("Valor unit.", PDType1Font.HELVETICA_BOLD, 8, MARGIN + 380, y);
            write("Subtotal", PDType1Font.HELVETICA_BOLD, 8, MARGIN + 470, y);
            y -= 14;
        }

        private void tableRow(String origen, String descripcion, String cantidad, String valorUnitario, String subtotal) throws IOException {
            ensureSpace(18);
            write(truncate(origen, 12), PDType1Font.HELVETICA, 8, MARGIN, y);
            write(truncate(descripcion, 48), PDType1Font.HELVETICA, 8, MARGIN + 70, y);
            write(cantidad, PDType1Font.HELVETICA, 8, MARGIN + 330, y);
            write(valorUnitario, PDType1Font.HELVETICA, 8, MARGIN + 380, y);
            write(subtotal, PDType1Font.HELVETICA, 8, MARGIN + 470, y);
            y -= 13;
        }

        private void footer() throws IOException {
            ensureSpace(24);
            y -= 8;
            write("Documento generado automaticamente por SGIE Club Boyaca. Valores expresados en pesos colombianos.",
                    PDType1Font.HELVETICA_OBLIQUE, 8, MARGIN, y);
        }

        private void space(float amount) throws IOException {
            ensureSpace(amount);
            y -= amount;
        }

        private void ensureSpace(float needed) throws IOException {
            if (y - needed < MARGIN) {
                newPage();
            }
        }

        private void newPage() throws IOException {
            if (content != null) {
                content.close();
            }
            page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            content = new PDPageContentStream(document, page);
            y = page.getMediaBox().getHeight() - MARGIN;
        }

        private void write(String text, PDType1Font font, int fontSize, float x, float yPosition) throws IOException {
            content.beginText();
            content.setFont(font, fontSize);
            content.newLineAtOffset(x, yPosition);
            content.showText(sanitizePdfText(text));
            content.endText();
        }

        private String truncate(String text, int length) {
            if (text == null) {
                return "";
            }
            return text.length() <= length ? text : text.substring(0, Math.max(0, length - 3)) + "...";
        }

        private String sanitizePdfText(String text) {
            if (text == null) {
                return "";
            }
            return text
                    .replace('\u00A0', ' ')
                    .replace("\r", " ")
                    .replace("\n", " ")
                    .replace("–", "-")
                    .replace("—", "-");
        }

        @Override
        public void close() throws IOException {
            if (content != null) {
                content.close();
                content = null;
            }
        }
    }

    private CotizacionView toView(Cotizacion cotizacion) {
        return new CotizacionView(
                cotizacion.getId(),
                cotizacion.getReservaId(),
                cotizacion.getUsuarioId(),
                cotizacion.getEstado(),
                cotizacion.isVigente(),
                cotizacion.getValorSubtotal(),
                cotizacion.getDescuento(),
                cotizacion.getValorTotal(),
                cotizacion.getObservaciones(),
                cotizacion.getItems().stream().map(this::toView).toList()
        );
    }

    private CotizacionItemView toView(CotizacionItem item) {
        return new CotizacionItemView(
                item.getId(),
                item.getTipoConcepto(),
                item.getOrigenId(),
                item.getDescripcion(),
                item.getPrecioBase(),
                item.getPrecioOverride(),
                item.getCantidad(),
                item.getSubtotal()
        );
    }
}
