package com.ejemplo.monolitomodular.cotizaciones.aplicacion.servicio;

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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
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
    private final CrearNotificacionUseCase crearNotificacionUseCase;
    private final EnviarCotizacionUseCase enviarCotizacionUseCase;
    private final CotizacionExcelDocumentGenerator excelDocumentGenerator;
    private final ObjectMapper objectMapper;

    public CotizacionOperacionApplicationService(
            CotizacionRepository cotizacionRepository,
            ReservaSalonRepository reservaSalonRepository,
            EventoRepository eventoRepository,
            ClienteRepository clienteRepository,
            SalonRepository salonRepository,
            TipoEventoRepository tipoEventoRepository,
            CrearNotificacionUseCase crearNotificacionUseCase,
            EnviarCotizacionUseCase enviarCotizacionUseCase,
            CotizacionExcelDocumentGenerator excelDocumentGenerator,
            ObjectMapper objectMapper
    ) {
        this.cotizacionRepository = cotizacionRepository;
        this.reservaSalonRepository = reservaSalonRepository;
        this.eventoRepository = eventoRepository;
        this.clienteRepository = clienteRepository;
        this.salonRepository = salonRepository;
        this.tipoEventoRepository = tipoEventoRepository;
        this.crearNotificacionUseCase = crearNotificacionUseCase;
        this.enviarCotizacionUseCase = enviarCotizacionUseCase;
        this.excelDocumentGenerator = excelDocumentGenerator;
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
                excelDocumentGenerator.generar(context.cotizacion(), context.evento(), context.reserva(), context.cliente(), context.salon())
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
        } catch (JsonProcessingException ex) {
            return "{}";
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

    private String money(BigDecimal value) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        format.setMaximumFractionDigits(0);
        return format.format(value == null ? BigDecimal.ZERO : value);
    }

    private String formatoFecha(LocalDateTime fecha) {
        return fecha == null ? "" : fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private record DocumentoContext(
            Cotizacion cotizacion,
            ReservaSalon reserva,
            Evento evento,
            Cliente cliente,
            Salon salon
    ) {
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
