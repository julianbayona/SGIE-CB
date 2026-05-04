package com.ejemplo.monolitomodular.pagos.aplicacion.servicio;

import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.Cotizacion;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.EstadoCotizacion;
import com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida.CotizacionRepository;
import com.ejemplo.monolitomodular.eventos.aplicacion.evento.EventoConfirmadoEvent;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.HistorialEstadoEvento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.EventoRepository;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.HistorialEstadoEventoRepository;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.ReservaSalonRepository;
import com.ejemplo.monolitomodular.pagos.aplicacion.dto.AnticipoView;
import com.ejemplo.monolitomodular.pagos.aplicacion.dto.RegistrarAnticipoCommand;
import com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada.RegistrarAnticipoUseCase;
import com.ejemplo.monolitomodular.pagos.dominio.modelo.Anticipo;
import com.ejemplo.monolitomodular.pagos.dominio.puerto.salida.AnticipoRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PagoApplicationService implements RegistrarAnticipoUseCase {

    private final AnticipoRepository anticipoRepository;
    private final CotizacionRepository cotizacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaSalonRepository reservaSalonRepository;
    private final EventoRepository eventoRepository;
    private final HistorialEstadoEventoRepository historialEstadoEventoRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PagoApplicationService(
            AnticipoRepository anticipoRepository,
            CotizacionRepository cotizacionRepository,
            UsuarioRepository usuarioRepository,
            ReservaSalonRepository reservaSalonRepository,
            EventoRepository eventoRepository,
            HistorialEstadoEventoRepository historialEstadoEventoRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.anticipoRepository = anticipoRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.reservaSalonRepository = reservaSalonRepository;
        this.eventoRepository = eventoRepository;
        this.historialEstadoEventoRepository = historialEstadoEventoRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public AnticipoView ejecutar(RegistrarAnticipoCommand command) {
        usuarioRepository.buscarPorId(command.usuarioId())
                .orElseThrow(() -> new DomainException("Usuario no encontrado"));
        Cotizacion cotizacion = cotizacionRepository.buscarPorId(command.cotizacionId())
                .orElseThrow(() -> new DomainException("Cotizacion no encontrada"));
        if (cotizacion.getEstado() != EstadoCotizacion.ACEPTADA) {
            throw new DomainException("Solo se pueden registrar anticipos sobre una cotizacion aceptada");
        }
        ReservaSalon reserva = reservaSalonRepository.buscarPorId(cotizacion.getReservaId())
                .orElseThrow(() -> new DomainException("Reserva asociada a la cotizacion no encontrada"));
        Evento evento = eventoRepository.buscarPorId(reserva.getEventoId())
                .orElseThrow(() -> new DomainException("Evento asociado a la cotizacion no encontrado"));

        BigDecimal totalActual = anticipoRepository.totalPorEventoId(evento.getId());
        Anticipo anticipo = Anticipo.nuevo(
                cotizacion.getId(),
                command.usuarioId(),
                command.valor(),
                command.metodoPago(),
                command.fechaPago(),
                command.observaciones()
        );
        BigDecimal nuevoTotal = totalActual.add(anticipo.getValor());
        if (nuevoTotal.compareTo(cotizacion.getValorTotal()) > 0) {
            throw new DomainException("El valor acumulado de anticipos no puede superar el total de la cotizacion");
        }

        Anticipo guardado = anticipoRepository.guardar(anticipo);
        confirmarEventoSiAplica(evento, command.usuarioId());
        return toView(guardado, nuevoTotal, cotizacion.getValorTotal());
    }

    private void confirmarEventoSiAplica(Evento evento, java.util.UUID usuarioId) {
        Evento confirmado = evento.confirmarConAnticipo();
        if (confirmado.getEstado() != evento.getEstado()) {
            eventoRepository.guardar(confirmado);
            historialEstadoEventoRepository.guardar(HistorialEstadoEvento.registrarCambio(
                    evento.getId(),
                    usuarioId,
                    evento.getEstado(),
                    confirmado.getEstado()
            ));
            eventPublisher.publishEvent(new EventoConfirmadoEvent(
                    confirmado.getId(),
                    confirmado.getClienteId(),
                    confirmado.getFechaHoraInicio(),
                    confirmado.getFechaHoraFin()
            ));
        }
    }

    private AnticipoView toView(Anticipo anticipo, BigDecimal totalPagado, BigDecimal valorTotalCotizacion) {
        return new AnticipoView(
                anticipo.getId(),
                anticipo.getCotizacionId(),
                anticipo.getUsuarioId(),
                anticipo.getValor(),
                anticipo.getMetodoPago(),
                anticipo.getFechaPago(),
                anticipo.getObservaciones(),
                totalPagado,
                valorTotalCotizacion.subtract(totalPagado)
        );
    }
}
