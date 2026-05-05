package com.ejemplo.monolitomodular.pagos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.pagos.dominio.modelo.Anticipo;
import com.ejemplo.monolitomodular.pagos.dominio.modelo.EventoAnticipoPendiente;
import com.ejemplo.monolitomodular.pagos.dominio.puerto.salida.AnticipoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class AnticipoJpaRepositoryAdapter implements AnticipoRepository {

    private final SpringDataAnticipoJpaRepository repository;

    public AnticipoJpaRepositoryAdapter(SpringDataAnticipoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Anticipo guardar(Anticipo anticipo) {
        return toDomain(repository.save(new AnticipoJpaEntity(
                anticipo.getId(),
                anticipo.getCotizacionId(),
                anticipo.getUsuarioId(),
                anticipo.getValor(),
                anticipo.getMetodoPago(),
                anticipo.getFechaPago(),
                anticipo.getObservaciones()
        )));
    }

    @Override
    public List<Anticipo> listarPorCotizacionId(UUID cotizacionId) {
        return repository.findByCotizacionIdOrderByFechaPagoAsc(cotizacionId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public BigDecimal totalPorCotizacionId(UUID cotizacionId) {
        return repository.totalPorCotizacionId(cotizacionId);
    }

    @Override
    public BigDecimal totalPorEventoId(UUID eventoId) {
        return repository.totalPorEventoId(eventoId);
    }

    @Override
    public List<EventoAnticipoPendiente> buscarEventosConAnticipoPendiente(LocalDateTime desde, LocalDateTime hasta, int limite) {
        return repository.buscarEventosConAnticipoPendiente(desde, hasta, PageRequest.of(0, limite)).stream()
                .map(this::toEventoAnticipoPendiente)
                .toList();
    }

    private Anticipo toDomain(AnticipoJpaEntity entity) {
        return Anticipo.reconstruir(
                entity.getId(),
                entity.getCotizacionId(),
                entity.getUsuarioId(),
                entity.getValor(),
                entity.getMetodoPago(),
                entity.getFechaPago(),
                entity.getObservaciones()
        );
    }

    private EventoAnticipoPendiente toEventoAnticipoPendiente(EventoAnticipoPendienteProjection projection) {
        return new EventoAnticipoPendiente(
                projection.getEventoId(),
                projection.getCotizacionId(),
                projection.getNombreCliente(),
                projection.getTelefonoCliente(),
                projection.getCorreoCliente(),
                projection.getFechaHoraInicio(),
                projection.getValorTotal(),
                projection.getTotalPagado()
        );
    }
}
