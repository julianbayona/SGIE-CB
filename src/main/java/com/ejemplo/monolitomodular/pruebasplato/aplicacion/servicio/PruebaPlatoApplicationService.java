package com.ejemplo.monolitomodular.pruebasplato.aplicacion.servicio;

import com.ejemplo.monolitomodular.clientes.dominio.modelo.Cliente;
import com.ejemplo.monolitomodular.clientes.dominio.puerto.salida.ClienteRepository;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.EventoRepository;
import com.ejemplo.monolitomodular.pruebasplato.aplicacion.dto.ProgramarPruebaPlatoCommand;
import com.ejemplo.monolitomodular.pruebasplato.aplicacion.dto.PruebaPlatoView;
import com.ejemplo.monolitomodular.pruebasplato.aplicacion.evento.PruebaPlatoProgramadaEvent;
import com.ejemplo.monolitomodular.pruebasplato.aplicacion.puerto.entrada.ProgramarPruebaPlatoUseCase;
import com.ejemplo.monolitomodular.pruebasplato.dominio.modelo.PruebaPlato;
import com.ejemplo.monolitomodular.pruebasplato.dominio.puerto.salida.PruebaPlatoRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PruebaPlatoApplicationService implements ProgramarPruebaPlatoUseCase {

    private final PruebaPlatoRepository pruebaPlatoRepository;
    private final EventoRepository eventoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PruebaPlatoApplicationService(
            PruebaPlatoRepository pruebaPlatoRepository,
            EventoRepository eventoRepository,
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.pruebaPlatoRepository = pruebaPlatoRepository;
        this.eventoRepository = eventoRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public PruebaPlatoView ejecutar(ProgramarPruebaPlatoCommand command) {
        usuarioRepository.buscarPorId(command.usuarioId())
                .orElseThrow(() -> new DomainException("Usuario no encontrado"));
        Evento evento = eventoRepository.buscarPorId(command.eventoId())
                .orElseThrow(() -> new DomainException("Evento no encontrado"));
        Cliente cliente = clienteRepository.buscarPorId(evento.getClienteId())
                .orElseThrow(() -> new DomainException("Cliente no encontrado"));

        PruebaPlato guardada = pruebaPlatoRepository.guardar(PruebaPlato.programar(
                evento.getId(),
                command.fechaRealizacion()
        ));
        eventPublisher.publishEvent(new PruebaPlatoProgramadaEvent(
                guardada.getId(),
                evento.getId(),
                cliente.getId(),
                cliente.getNombreCompleto(),
                cliente.getTelefono(),
                guardada.getFechaRealizacion()
        ));
        return toView(guardada);
    }

    private PruebaPlatoView toView(PruebaPlato pruebaPlato) {
        return new PruebaPlatoView(
                pruebaPlato.getId(),
                pruebaPlato.getEventoId(),
                pruebaPlato.getFechaRealizacion(),
                pruebaPlato.getEstado()
        );
    }
}
