package com.ejemplo.monolitomodular.pagos.aplicacion.servicio;

import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.Cotizacion;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.CotizacionItem;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.EstadoCotizacion;
import com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida.CotizacionRepository;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;
import com.ejemplo.monolitomodular.eventos.aplicacion.evento.EventoConfirmadoEvent;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.HistorialEstadoEvento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.EventoRepository;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.HistorialEstadoEventoRepository;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.ReservaSalonRepository;
import com.ejemplo.monolitomodular.pagos.aplicacion.dto.AnticipoView;
import com.ejemplo.monolitomodular.pagos.aplicacion.dto.RegistrarAnticipoCommand;
import com.ejemplo.monolitomodular.pagos.dominio.modelo.Anticipo;
import com.ejemplo.monolitomodular.pagos.dominio.modelo.EventoAnticipoPendiente;
import com.ejemplo.monolitomodular.pagos.dominio.puerto.salida.AnticipoRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.Usuario;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PagoApplicationServiceTest {

    @Test
    void deberiaRegistrarAnticipoSinConfirmarEventoAutomaticamente() {
        EscenarioPago escenario = escenario(cotizacionAceptada(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));

        AnticipoView view = escenario.service().ejecutar(command(
                escenario.cotizacion().getId(),
                escenario.usuario().getId(),
                new BigDecimal("500000.00")
        ));

        assertEquals(new BigDecimal("500000.00"), view.totalPagado());
        assertEquals(new BigDecimal("1500000.00"), view.saldoPendiente());
        assertEquals(EstadoEvento.COTIZACION_APROBADA, escenario.eventoRepository().estado());
        assertEquals(0, escenario.historialRepository().total());
        assertEquals(0, escenario.eventPublisher().total());
    }

    @Test
    void noDeberiaPermitirAnticiposQueSuperenTotalCotizacion() {
        EscenarioPago escenario = escenario(cotizacionAceptada(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));

        escenario.service().ejecutar(command(
                escenario.cotizacion().getId(),
                escenario.usuario().getId(),
                new BigDecimal("1500000.00")
        ));

        assertThrows(DomainException.class, () -> escenario.service().ejecutar(command(
                escenario.cotizacion().getId(),
                escenario.usuario().getId(),
                new BigDecimal("600000.00")
        )));
    }

    @Test
    void noDeberiaRegistrarAnticipoSiCotizacionNoEstaAceptada() {
        UUID cotizacionId = UUID.randomUUID();
        UUID reservaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        Cotizacion cotizacion = Cotizacion.crearBorrador(
                cotizacionId,
                reservaId,
                usuarioId,
                BigDecimal.ZERO,
                null,
                List.of(item(cotizacionId))
        );
        EscenarioPago escenario = escenario(cotizacion);

        assertThrows(DomainException.class, () -> escenario.service().ejecutar(command(
                cotizacion.getId(),
                escenario.usuario().getId(),
                new BigDecimal("500000.00")
        )));
    }

    @Test
    void deberiaPermitirVariosAnticiposSinConfirmarEventoAutomaticamente() {
        EscenarioPago escenario = escenario(cotizacionAceptada(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));

        escenario.service().ejecutar(command(
                escenario.cotizacion().getId(),
                escenario.usuario().getId(),
                new BigDecimal("500000.00")
        ));
        AnticipoView segundo = escenario.service().ejecutar(command(
                escenario.cotizacion().getId(),
                escenario.usuario().getId(),
                new BigDecimal("700000.00")
        ));

        assertEquals(new BigDecimal("1200000.00"), segundo.totalPagado());
        assertEquals(new BigDecimal("800000.00"), segundo.saldoPendiente());
        assertEquals(EstadoEvento.COTIZACION_APROBADA, escenario.eventoRepository().estado());
        assertEquals(0, escenario.historialRepository().total());
        assertEquals(0, escenario.eventPublisher().total());
    }

    @Test
    void deberiaCalcularSaldoConAnticiposPreviosDelEvento() {
        EscenarioPago escenario = escenario(cotizacionAceptada(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        escenario.anticipoRepository().agregar(Anticipo.nuevo(
                UUID.randomUUID(),
                escenario.usuario().getId(),
                new BigDecimal("400000.00"),
                "TRANSFERENCIA",
                LocalDate.of(2026, 8, 20),
                "Anticipo de cotizacion anterior"
        ));

        AnticipoView view = escenario.service().ejecutar(command(
                escenario.cotizacion().getId(),
                escenario.usuario().getId(),
                new BigDecimal("300000.00")
        ));

        assertEquals(new BigDecimal("700000.00"), view.totalPagado());
        assertEquals(new BigDecimal("1300000.00"), view.saldoPendiente());
    }


    private static EscenarioPago escenario(Cotizacion cotizacion) {
        Usuario usuario = Usuario.reconstruir(cotizacion.getUsuarioId(), "Admin", "$2a$hash", RolUsuario.ADMINISTRADOR, true);
        ReservaSalon reserva = reserva(cotizacion.getReservaId(), usuario.getId());
        EventoRepositoryStub eventoRepository = new EventoRepositoryStub(evento(reserva, EstadoEvento.COTIZACION_APROBADA));
        HistorialRepositoryStub historialRepository = new HistorialRepositoryStub();
        AnticipoRepositoryStub anticipoRepository = new AnticipoRepositoryStub();
        ApplicationEventPublisherStub eventPublisher = new ApplicationEventPublisherStub();
        PagoApplicationService service = new PagoApplicationService(
                anticipoRepository,
                new CotizacionRepositoryStub(cotizacion),
                new UsuarioRepositoryStub(usuario),
                new ReservaSalonRepositoryStub(reserva),
                eventoRepository
        );
        return new EscenarioPago(usuario, cotizacion, service, eventoRepository, historialRepository, anticipoRepository, eventPublisher);
    }

    private static RegistrarAnticipoCommand command(UUID cotizacionId, UUID usuarioId, BigDecimal valor) {
        return new RegistrarAnticipoCommand(
                cotizacionId,
                usuarioId,
                valor,
                "TRANSFERENCIA",
                LocalDate.of(2026, 9, 1),
                null
        );
    }

    private static Cotizacion cotizacionAceptada(UUID cotizacionId, UUID reservaId, UUID usuarioId) {
        return Cotizacion.crearBorrador(
                        cotizacionId,
                        reservaId,
                        usuarioId,
                        BigDecimal.ZERO,
                        null,
                        List.of(item(cotizacionId))
                )
                .generarDocumento()
                .enviar()
                .aceptar();
    }

    private static CotizacionItem item(UUID cotizacionId) {
        return CotizacionItem.nuevo(
                cotizacionId,
                "MENU",
                UUID.randomUUID(),
                "Almuerzo ejecutivo",
                new BigDecimal("20000.00"),
                null,
                100
        );
    }

    private static ReservaSalon reserva(UUID reservaId, UUID usuarioId) {
        return ReservaSalon.reconstruir(
                reservaId,
                reservaId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                100,
                LocalDateTime.of(2026, 9, 10, 8, 0),
                LocalDateTime.of(2026, 9, 10, 12, 0),
                1,
                true,
                usuarioId
        );
    }

    private static Evento evento(ReservaSalon reserva, EstadoEvento estado) {
        return Evento.reconstruir(
                reserva.getEventoId(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                reserva.getCreadoPor(),
                reserva.getFechaHoraInicio(),
                reserva.getFechaHoraFin(),
                estado,
                null
        );
    }

    private record EscenarioPago(
            Usuario usuario,
            Cotizacion cotizacion,
            PagoApplicationService service,
            EventoRepositoryStub eventoRepository,
            HistorialRepositoryStub historialRepository,
            AnticipoRepositoryStub anticipoRepository,
            ApplicationEventPublisherStub eventPublisher
    ) {
    }

    private static class AnticipoRepositoryStub implements AnticipoRepository {

        private final List<Anticipo> anticipos = new ArrayList<>();

        void agregar(Anticipo anticipo) {
            anticipos.add(anticipo);
        }

        @Override
        public Anticipo guardar(Anticipo anticipo) {
            anticipos.add(anticipo);
            return anticipo;
        }

        @Override
        public List<Anticipo> listarPorCotizacionId(UUID cotizacionId) {
            return anticipos.stream()
                    .filter(anticipo -> anticipo.getCotizacionId().equals(cotizacionId))
                    .toList();
        }

        @Override
        public BigDecimal totalPorCotizacionId(UUID cotizacionId) {
            return listarPorCotizacionId(cotizacionId).stream()
                    .map(Anticipo::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        @Override
        public BigDecimal totalPorEventoId(UUID eventoId) {
            return anticipos.stream()
                    .map(Anticipo::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        @Override
        public List<EventoAnticipoPendiente> buscarEventosConAnticipoPendiente(LocalDateTime desde, LocalDateTime hasta, int limite) {
            return List.of();
        }
    }

    private static class CotizacionRepositoryStub implements CotizacionRepository {

        private final Cotizacion cotizacion;

        private CotizacionRepositoryStub(Cotizacion cotizacion) {
            this.cotizacion = cotizacion;
        }

        @Override
        public Cotizacion guardar(Cotizacion cotizacion) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Cotizacion> buscarPorId(UUID id) {
            return cotizacion.getId().equals(id) ? Optional.of(cotizacion) : Optional.empty();
        }

        @Override
        public Optional<Cotizacion> buscarActivaPorReservaId(UUID reservaId) {
            return Optional.empty();
        }

        @Override
        public Optional<Cotizacion> buscarUltimaPorReservaRaizId(UUID reservaRaizId) {
            return Optional.empty();
        }

        @Override
        public Optional<Cotizacion> buscarAceptadaVigentePorEventoId(UUID eventoId) {
            return Optional.empty();
        }

        @Override
        public List<Cotizacion> listarPorEventoId(UUID eventoId) {
            return List.of(cotizacion);
        }

        @Override
        public void desactualizarActivasPorReservaId(UUID reservaId) {
        }
    }

    private static class UsuarioRepositoryStub implements UsuarioRepository {

        private final Usuario usuario;

        private UsuarioRepositoryStub(Usuario usuario) {
            this.usuario = usuario;
        }

        @Override
        public Usuario guardar(Usuario usuario) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Usuario> buscarPorId(UUID id) {
            return usuario.getId().equals(id) ? Optional.of(usuario) : Optional.empty();
        }
    }

    private static class ReservaSalonRepositoryStub implements ReservaSalonRepository {

        private final ReservaSalon reserva;

        private ReservaSalonRepositoryStub(ReservaSalon reserva) {
            this.reserva = reserva;
        }

        @Override
        public List<ReservaSalon> guardarTodas(List<ReservaSalon> reservas) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReservaSalon guardar(ReservaSalon reserva) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean existeConflicto(UUID salonId, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin) {
            return false;
        }

        @Override
        public boolean existeConflicto(UUID salonId, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, UUID reservaRaizIdExcluida) {
            return false;
        }

        @Override
        public boolean existeConflictoParaEvento(UUID eventoId) {
            return false;
        }

        @Override
        public List<ReservaSalon> listarPorEvento(UUID eventoId) {
            return List.of(reserva);
        }

        @Override
        public Set<UUID> buscarSalonesOcupados(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin) {
            return Set.of();
        }

        @Override
        public Optional<ReservaSalon> buscarVigentePorEventoYSalon(UUID eventoId, UUID salonId) {
            return Optional.empty();
        }

        @Override
        public Optional<ReservaSalon> buscarPorId(UUID id) {
            return reserva.getId().equals(id) ? Optional.of(reserva) : Optional.empty();
        }

        @Override
        public Optional<ReservaSalon> buscarVigentePorRaizId(UUID reservaRaizId) {
            return Optional.empty();
        }

        @Override
        public void desactivarReservaVigente(UUID reservaRaizId) {
            throw new UnsupportedOperationException();
        }
    }

    private static class EventoRepositoryStub implements EventoRepository {

        private Evento evento;

        private EventoRepositoryStub(Evento evento) {
            this.evento = evento;
        }

        @Override
        public Evento guardar(Evento evento) {
            this.evento = evento;
            return evento;
        }

        @Override
        public Optional<Evento> buscarPorId(UUID id) {
            return evento.getId().equals(id) ? Optional.of(evento) : Optional.empty();
        }

        @Override
        public List<Evento> listar() {
            return List.of(evento);
        }

        EstadoEvento estado() {
            return evento.getEstado();
        }

        Evento evento() {
            return evento;
        }
    }

    private static class HistorialRepositoryStub implements HistorialEstadoEventoRepository {

        private final List<HistorialEstadoEvento> historiales = new ArrayList<>();

        @Override
        public HistorialEstadoEvento guardar(HistorialEstadoEvento historialEstadoEvento) {
            historiales.add(historialEstadoEvento);
            return historialEstadoEvento;
        }

        int total() {
            return historiales.size();
        }
    }

    private static class ApplicationEventPublisherStub implements ApplicationEventPublisher {

        private final List<Object> eventos = new ArrayList<>();

        @Override
        public void publishEvent(Object event) {
            eventos.add(event);
        }

        int total() {
            return eventos.size();
        }

        Object ultimo() {
            return eventos.get(eventos.size() - 1);
        }
    }
}
