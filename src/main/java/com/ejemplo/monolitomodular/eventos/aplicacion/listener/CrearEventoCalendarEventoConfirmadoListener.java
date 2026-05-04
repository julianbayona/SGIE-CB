package com.ejemplo.monolitomodular.eventos.aplicacion.listener;

import com.ejemplo.monolitomodular.calendario.dominio.modelo.EventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.OrigenEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.TipoOperacionCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.EventoCalendarRepository;
import com.ejemplo.monolitomodular.clientes.dominio.modelo.Cliente;
import com.ejemplo.monolitomodular.clientes.dominio.puerto.salida.ClienteRepository;
import com.ejemplo.monolitomodular.eventos.aplicacion.evento.EventoConfirmadoEvent;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CrearEventoCalendarEventoConfirmadoListener {

    private final EventoCalendarRepository eventoCalendarRepository;
    private final ClienteRepository clienteRepository;
    private final String correosAsistentes;

    public CrearEventoCalendarEventoConfirmadoListener(
            EventoCalendarRepository eventoCalendarRepository,
            ClienteRepository clienteRepository,
            @Value("${sgie.calendario.evento-confirmado.asistentes-correos:}") String correosAsistentes
    ) {
        this.eventoCalendarRepository = eventoCalendarRepository;
        this.clienteRepository = clienteRepository;
        this.correosAsistentes = correosAsistentes;
    }

    @EventListener
    public void manejar(EventoConfirmadoEvent event) {
        Cliente cliente = clienteRepository.buscarPorId(event.clienteId())
                .orElseThrow(() -> new DomainException("Cliente no encontrado para calendar de evento confirmado"));
        eventoCalendarRepository.guardar(EventoCalendar.pendiente(
                OrigenEventoCalendar.EVENTO,
                event.eventoId(),
                event.eventoId(),
                TipoOperacionCalendar.CREAR,
                payload(event, cliente)
        ));
    }

    private String payload(EventoConfirmadoEvent event, Cliente cliente) {
        return """
                {"resumen":"Evento confirmado Club Boyaca","fechaInicio":"%s","fechaFin":"%s","origen":"EVENTO_CONFIRMADO","attendees":%s}
                """.formatted(event.fechaHoraInicio(), event.fechaHoraFin(), attendeesJson(cliente)).trim();
    }

    private String attendeesJson(Cliente cliente) {
        List<String> correosConfigurados = Arrays.stream(correosAsistentes.split(","))
                .map(String::trim)
                .filter(correo -> !correo.isBlank())
                .toList();
        return StreamConcat.concat(List.of(cliente.getCorreo()), correosConfigurados).stream()
                .map(correo -> "{\"email\":\"%s\"}".formatted(correo))
                .toList()
                .toString();
    }

    private static class StreamConcat {

        private static List<String> concat(List<String> principal, List<String> adicionales) {
            return java.util.stream.Stream.concat(principal.stream(), adicionales.stream()).toList();
        }
    }
}
