package com.ejemplo.monolitomodular.pagos.aplicacion.servicio;

import com.ejemplo.monolitomodular.clientes.dominio.modelo.Cliente;
import com.ejemplo.monolitomodular.clientes.dominio.puerto.salida.ClienteRepository;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.Cotizacion;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.CotizacionItem;
import com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida.CotizacionRepository;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.HistorialEstadoEvento;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.EventoRepository;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.HistorialEstadoEventoRepository;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.CrearNotificacionCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.NotificacionView;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.CrearNotificacionUseCase;
import com.ejemplo.monolitomodular.pagos.aplicacion.dto.ProgramarRecordatorioAnticipoCommand;
import com.ejemplo.monolitomodular.pagos.dominio.modelo.Anticipo;
import com.ejemplo.monolitomodular.pagos.dominio.modelo.EventoAnticipoPendiente;
import com.ejemplo.monolitomodular.pagos.dominio.modelo.RecordatorioAnticipo;
import com.ejemplo.monolitomodular.pagos.dominio.puerto.salida.AnticipoRepository;
import com.ejemplo.monolitomodular.pagos.dominio.puerto.salida.RecordatorioAnticipoRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.Usuario;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecordatorioAnticipoProgramadoApplicationServiceTest {

    @Test
    void deberiaMarcarEventoPendienteAnticipoAlProgramarPrimerRecordatorio() {
        Escenario escenario = escenario(BigDecimal.ZERO, false);

        escenario.service().ejecutar(new ProgramarRecordatorioAnticipoCommand(
                escenario.evento().getId(),
                escenario.usuario().getId(),
                LocalDate.now().plusDays(1)
        ));

        assertEquals(EstadoEvento.PENDIENTE_ANTICIPO, escenario.eventoRepository().estado());
        assertEquals(1, escenario.historialRepository().total());
    }

    @Test
    void deberiaPermitirRecordatorioSiYaHayAnticiposPeroQuedaSaldoPendiente() {
        Escenario escenario = escenario(new BigDecimal("500000.00"), false);

        escenario.service().ejecutar(new ProgramarRecordatorioAnticipoCommand(
                escenario.evento().getId(),
                escenario.usuario().getId(),
                LocalDate.now().plusDays(1)
        ));

        assertEquals(1, escenario.recordatorioRepository().total());
        assertEquals(EstadoEvento.PENDIENTE_ANTICIPO, escenario.eventoRepository().estado());
    }

    @Test
    void noDeberiaProgramarRecordatorioSiElEventoYaEstaPagadoTotalmente() {
        Escenario escenario = escenario(new BigDecimal("2000000.00"), false);

        assertThrows(DomainException.class, () -> escenario.service().ejecutar(new ProgramarRecordatorioAnticipoCommand(
                escenario.evento().getId(),
                escenario.usuario().getId(),
                LocalDate.now().plusDays(1)
        )));

        assertEquals(0, escenario.recordatorioRepository().total());
        assertEquals(EstadoEvento.COTIZACION_APROBADA, escenario.eventoRepository().estado());
    }

    @Test
    void noDeberiaCambiarEstadoSiYaExisteRecordatorioPendienteEnLaMismaFecha() {
        Escenario escenario = escenario(BigDecimal.ZERO, true);

        assertThrows(DomainException.class, () -> escenario.service().ejecutar(new ProgramarRecordatorioAnticipoCommand(
                escenario.evento().getId(),
                escenario.usuario().getId(),
                LocalDate.now().plusDays(1)
        )));

        assertEquals(EstadoEvento.COTIZACION_APROBADA, escenario.eventoRepository().estado());
        assertEquals(0, escenario.historialRepository().total());
    }

    private static Escenario escenario(BigDecimal totalAnticiposEvento, boolean existeRecordatorio) {
        Usuario usuario = Usuario.nuevo("Admin", "$2a$hash", RolUsuario.ADMINISTRADOR);
        Evento evento = Evento.reconstruir(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                usuario.getId(),
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(10).plusHours(4),
                EstadoEvento.COTIZACION_APROBADA,
                null
        );
        Cotizacion cotizacion = Cotizacion.crearBorrador(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        usuario.getId(),
                        BigDecimal.ZERO,
                        null,
                        List.of(CotizacionItem.nuevo(
                                UUID.randomUUID(),
                                "MENU",
                                UUID.randomUUID(),
                                "Almuerzo ejecutivo",
                                new BigDecimal("20000.00"),
                                null,
                                100
                        ))
                )
                .generarDocumento()
                .enviar()
                .aceptar();

        EventoRepositoryStub eventoRepository = new EventoRepositoryStub(evento);
        HistorialRepositoryStub historialRepository = new HistorialRepositoryStub();
        RecordatorioRepositoryStub recordatorioRepository = new RecordatorioRepositoryStub(existeRecordatorio);
        RecordatorioAnticipoProgramadoApplicationService service = new RecordatorioAnticipoProgramadoApplicationService(
                recordatorioRepository,
                eventoRepository,
                historialRepository,
                new ClienteRepositoryStub(),
                new UsuarioRepositoryStub(usuario),
                new CotizacionRepositoryStub(cotizacion),
                new AnticipoRepositoryStub(totalAnticiposEvento),
                new CrearNotificacionUseCaseStub(),
                new ObjectMapper()
        );
        return new Escenario(usuario, evento, service, eventoRepository, historialRepository, recordatorioRepository);
    }

    private record Escenario(
            Usuario usuario,
            Evento evento,
            RecordatorioAnticipoProgramadoApplicationService service,
            EventoRepositoryStub eventoRepository,
            HistorialRepositoryStub historialRepository,
            RecordatorioRepositoryStub recordatorioRepository
    ) {
    }

    private static class RecordatorioRepositoryStub implements RecordatorioAnticipoRepository {

        private final boolean existeRecordatorio;
        private final List<RecordatorioAnticipo> recordatorios = new ArrayList<>();

        private RecordatorioRepositoryStub(boolean existeRecordatorio) {
            this.existeRecordatorio = existeRecordatorio;
        }

        @Override
        public RecordatorioAnticipo guardar(RecordatorioAnticipo recordatorio) {
            recordatorios.add(recordatorio);
            return recordatorio;
        }

        @Override
        public Optional<RecordatorioAnticipo> buscarPorId(UUID id) {
            return recordatorios.stream().filter(recordatorio -> recordatorio.getId().equals(id)).findFirst();
        }

        @Override
        public boolean existePendientePorEventoYFecha(UUID eventoId, LocalDate fechaRecordatorio) {
            return existeRecordatorio;
        }

        @Override
        public List<RecordatorioAnticipo> buscarPendientesHasta(LocalDate fechaReferencia, int limite) {
            return List.of();
        }

        int total() {
            return recordatorios.size();
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

    private static class UsuarioRepositoryStub implements UsuarioRepository {

        private final Usuario usuario;

        private UsuarioRepositoryStub(Usuario usuario) {
            this.usuario = usuario;
        }

        @Override
        public Usuario guardar(Usuario usuario) {
            return usuario;
        }

        @Override
        public Optional<Usuario> buscarPorId(UUID id) {
            return usuario.getId().equals(id) ? Optional.of(usuario) : Optional.empty();
        }
    }

    private static class CotizacionRepositoryStub implements CotizacionRepository {

        private final Cotizacion cotizacion;

        private CotizacionRepositoryStub(Cotizacion cotizacion) {
            this.cotizacion = cotizacion;
        }

        @Override
        public Cotizacion guardar(Cotizacion cotizacion) {
            return cotizacion;
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
            return Optional.of(cotizacion);
        }

        @Override
        public List<Cotizacion> listarPorEventoId(UUID eventoId) {
            return List.of(cotizacion);
        }

        @Override
        public void desactualizarActivasPorReservaId(UUID reservaId) {
        }
    }

    private static class AnticipoRepositoryStub implements AnticipoRepository {

        private final BigDecimal totalEvento;

        private AnticipoRepositoryStub(BigDecimal totalEvento) {
            this.totalEvento = totalEvento;
        }

        @Override
        public Anticipo guardar(Anticipo anticipo) {
            return anticipo;
        }

        @Override
        public List<Anticipo> listarPorCotizacionId(UUID cotizacionId) {
            return List.of();
        }

        @Override
        public BigDecimal totalPorCotizacionId(UUID cotizacionId) {
            return BigDecimal.ZERO;
        }

        @Override
        public BigDecimal totalPorEventoId(UUID eventoId) {
            return totalEvento;
        }

        @Override
        public List<EventoAnticipoPendiente> buscarEventosConAnticipoPendiente(LocalDateTime desde, LocalDateTime hasta, int limite) {
            return List.of();
        }
    }

    private static class ClienteRepositoryStub implements ClienteRepository {

        @Override
        public Cliente guardar(Cliente cliente) {
            return cliente;
        }

        @Override
        public Optional<Cliente> buscarPorId(UUID id) {
            return Optional.empty();
        }

        @Override
        public Optional<Cliente> buscarPorCedula(String cedula) {
            return Optional.empty();
        }

        @Override
        public List<Cliente> listar() {
            return List.of();
        }

        @Override
        public List<Cliente> buscarPorFiltro(String filtro) {
            return List.of();
        }
    }

    private static class CrearNotificacionUseCaseStub implements CrearNotificacionUseCase {

        @Override
        public NotificacionView ejecutar(CrearNotificacionCommand command) {
            throw new UnsupportedOperationException();
        }
    }
}
