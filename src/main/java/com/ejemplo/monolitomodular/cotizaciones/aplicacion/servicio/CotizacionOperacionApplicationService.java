package com.ejemplo.monolitomodular.cotizaciones.aplicacion.servicio;

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
import java.time.LocalDateTime;
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
    private final CrearNotificacionUseCase crearNotificacionUseCase;
    private final EnviarCotizacionUseCase enviarCotizacionUseCase;
    private final ObjectMapper objectMapper;

    public CotizacionOperacionApplicationService(
            CotizacionRepository cotizacionRepository,
            ReservaSalonRepository reservaSalonRepository,
            EventoRepository eventoRepository,
            ClienteRepository clienteRepository,
            CrearNotificacionUseCase crearNotificacionUseCase,
            EnviarCotizacionUseCase enviarCotizacionUseCase,
            ObjectMapper objectMapper
    ) {
        this.cotizacionRepository = cotizacionRepository;
        this.reservaSalonRepository = reservaSalonRepository;
        this.eventoRepository = eventoRepository;
        this.clienteRepository = clienteRepository;
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
        Cotizacion cotizacion = buscarCotizacion(cotizacionId);
        Evento evento = evento(cotizacion);
        Cliente cliente = clienteRepository.buscarPorId(evento.getClienteId())
                .orElseThrow(() -> new DomainException("Cliente no encontrado"));
        return new DocumentoCotizacionView(
                "cotizacion-" + cotizacion.getId() + ".pdf",
                "application/pdf",
                generarPdf(cotizacion, evento, cliente)
        );
    }

    @Override
    @Transactional
    public CotizacionView enviarPorEmail(UUID cotizacionId) {
        Cotizacion cotizacion = buscarCotizacion(cotizacionId);
        CotizacionView view = switch (cotizacion.getEstado()) {
            case GENERADA -> enviarCotizacionUseCase.enviar(cotizacionId);
            case ENVIADA, ACEPTADA -> toView(cotizacion);
            default -> throw new DomainException("Solo una cotizacion generada, enviada o aceptada puede enviarse por email");
        };
        Evento evento = evento(cotizacion);
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
        ReservaSalon reserva = reservaSalonRepository.buscarPorId(cotizacion.getReservaId())
                .orElseThrow(() -> new DomainException("Reserva asociada a la cotizacion no encontrada"));
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

    private byte[] generarPdf(Cotizacion cotizacion, Evento evento, Cliente cliente) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                float y = 740;
                escribir(content, "Cotizacion Club Boyaca", 50, y, 16, true);
                y -= 30;
                escribir(content, "Cotizacion: " + cotizacion.getId(), 50, y, 10, false);
                y -= 16;
                escribir(content, "Cliente: " + cliente.getNombreCompleto(), 50, y, 10, false);
                y -= 16;
                escribir(content, "Fecha evento: " + evento.getFechaHoraInicio(), 50, y, 10, false);
                y -= 28;
                escribir(content, "Items", 50, y, 12, true);
                y -= 18;
                for (CotizacionItem item : cotizacion.getItems()) {
                    if (y < 90) {
                        escribir(content, "Continua en detalle digital del sistema.", 50, y, 10, false);
                        break;
                    }
                    escribir(content, lineaItem(item), 50, y, 9, false);
                    y -= 14;
                }
                y -= 16;
                escribir(content, "Subtotal: " + moneda(cotizacion.getValorSubtotal()), 50, y, 11, false);
                y -= 16;
                escribir(content, "Descuento: " + moneda(cotizacion.getDescuento()), 50, y, 11, false);
                y -= 16;
                escribir(content, "Total: " + moneda(cotizacion.getValorTotal()), 50, y, 12, true);
            }
            document.save(output);
            return output.toByteArray();
        } catch (IOException ex) {
            throw new DomainException("No se pudo generar el documento de cotizacion");
        }
    }

    private String lineaItem(CotizacionItem item) {
        return recortar("%s | %s | Cant: %d | Subtotal: %s".formatted(
                item.getTipoConcepto(),
                item.getDescripcion(),
                item.getCantidad(),
                moneda(item.getSubtotal())
        ), 95);
    }

    private String recortar(String valor, int max) {
        return valor.length() <= max ? valor : valor.substring(0, max - 3) + "...";
    }

    private String moneda(BigDecimal valor) {
        return "$" + valor;
    }

    private void escribir(PDPageContentStream content, String texto, float x, float y, int size, boolean bold) throws IOException {
        content.beginText();
        content.setFont(bold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, size);
        content.newLineAtOffset(x, y);
        content.showText(textoPdf(texto));
        content.endText();
    }

    private String textoPdf(String texto) {
        return texto == null ? "" : texto.replaceAll("[^\\x20-\\x7E]", "?");
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
