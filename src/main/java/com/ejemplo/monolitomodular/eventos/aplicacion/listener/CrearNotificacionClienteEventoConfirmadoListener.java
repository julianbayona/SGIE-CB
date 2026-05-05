package com.ejemplo.monolitomodular.eventos.aplicacion.listener;

import com.ejemplo.monolitomodular.clientes.dominio.modelo.Cliente;
import com.ejemplo.monolitomodular.clientes.dominio.puerto.salida.ClienteRepository;
import com.ejemplo.monolitomodular.eventos.aplicacion.evento.EventoConfirmadoEvent;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.CrearNotificacionCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.CrearNotificacionUseCase;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CrearNotificacionClienteEventoConfirmadoListener {

    private final ClienteRepository clienteRepository;
    private final CrearNotificacionUseCase crearNotificacionUseCase;

    public CrearNotificacionClienteEventoConfirmadoListener(
            ClienteRepository clienteRepository,
            CrearNotificacionUseCase crearNotificacionUseCase
    ) {
        this.clienteRepository = clienteRepository;
        this.crearNotificacionUseCase = crearNotificacionUseCase;
    }

    @EventListener
    public void manejar(EventoConfirmadoEvent event) {
        Cliente cliente = clienteRepository.buscarPorId(event.clienteId())
                .orElseThrow(() -> new DomainException("Cliente no encontrado para notificacion de evento confirmado"));
        crearNotificacionUseCase.ejecutar(new CrearNotificacionCommand(
                event.eventoId(),
                TipoNotificacion.EVENTO_CONFIRMADO_CLIENTE,
                LocalDateTime.now(),
                payload(event, cliente),
                List.of(new CrearNotificacionCommand.Destinatario(null, cliente.getTelefono(), cliente.getCorreo()))
        ));
    }

    private String payload(EventoConfirmadoEvent event, Cliente cliente) {
        return """
                {"tipo":"EVENTO_CONFIRMADO","cliente":"%s","fechaInicio":"%s","fechaFin":"%s"}
                """.formatted(cliente.getNombreCompleto(), event.fechaHoraInicio(), event.fechaHoraFin()).trim();
    }
}
