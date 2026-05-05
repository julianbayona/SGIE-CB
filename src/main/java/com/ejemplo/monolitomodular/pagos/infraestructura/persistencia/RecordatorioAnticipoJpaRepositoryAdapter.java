package com.ejemplo.monolitomodular.pagos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.pagos.dominio.modelo.EstadoRecordatorioAnticipo;
import com.ejemplo.monolitomodular.pagos.dominio.modelo.RecordatorioAnticipo;
import com.ejemplo.monolitomodular.pagos.dominio.puerto.salida.RecordatorioAnticipoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RecordatorioAnticipoJpaRepositoryAdapter implements RecordatorioAnticipoRepository {

    private final SpringDataRecordatorioAnticipoJpaRepository repository;

    public RecordatorioAnticipoJpaRepositoryAdapter(SpringDataRecordatorioAnticipoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public RecordatorioAnticipo guardar(RecordatorioAnticipo recordatorio) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = repository.findById(recordatorio.getId())
                .map(RecordatorioAnticipoJpaEntity::getCreatedAt)
                .orElse(now);
        return toDomain(repository.save(new RecordatorioAnticipoJpaEntity(
                recordatorio.getId(),
                recordatorio.getEventoId(),
                recordatorio.getUsuarioId(),
                recordatorio.getFechaRecordatorio(),
                recordatorio.getEstado(),
                recordatorio.getNotificacionId(),
                createdAt,
                now
        )));
    }

    @Override
    public Optional<RecordatorioAnticipo> buscarPorId(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existePendientePorEventoYFecha(UUID eventoId, LocalDate fechaRecordatorio) {
        return repository.existsByEventoIdAndFechaRecordatorioAndEstado(
                eventoId,
                fechaRecordatorio,
                EstadoRecordatorioAnticipo.PENDIENTE
        );
    }

    @Override
    public List<RecordatorioAnticipo> buscarPendientesHasta(LocalDate fechaReferencia, int limite) {
        return repository.findByEstadoAndFechaRecordatorioLessThanEqualOrderByFechaRecordatorioAscCreatedAtAsc(
                        EstadoRecordatorioAnticipo.PENDIENTE,
                        fechaReferencia,
                        PageRequest.of(0, limite)
                ).stream()
                .map(this::toDomain)
                .toList();
    }

    private RecordatorioAnticipo toDomain(RecordatorioAnticipoJpaEntity entity) {
        return RecordatorioAnticipo.reconstruir(
                entity.getId(),
                entity.getEventoId(),
                entity.getUsuarioId(),
                entity.getFechaRecordatorio(),
                entity.getEstado(),
                entity.getNotificacionId()
        );
    }
}
