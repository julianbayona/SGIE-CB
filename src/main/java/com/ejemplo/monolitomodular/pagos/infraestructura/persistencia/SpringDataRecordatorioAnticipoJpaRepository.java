package com.ejemplo.monolitomodular.pagos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.pagos.dominio.modelo.EstadoRecordatorioAnticipo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SpringDataRecordatorioAnticipoJpaRepository extends JpaRepository<RecordatorioAnticipoJpaEntity, UUID> {

    boolean existsByEventoIdAndFechaRecordatorioAndEstado(UUID eventoId, LocalDate fechaRecordatorio, EstadoRecordatorioAnticipo estado);

    List<RecordatorioAnticipoJpaEntity> findByEstadoAndFechaRecordatorioLessThanEqualOrderByFechaRecordatorioAscCreatedAtAsc(
            EstadoRecordatorioAnticipo estado,
            LocalDate fechaReferencia,
            Pageable pageable
    );
}
