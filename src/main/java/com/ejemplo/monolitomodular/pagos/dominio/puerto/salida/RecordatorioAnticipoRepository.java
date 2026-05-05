package com.ejemplo.monolitomodular.pagos.dominio.puerto.salida;

import com.ejemplo.monolitomodular.pagos.dominio.modelo.RecordatorioAnticipo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecordatorioAnticipoRepository {

    RecordatorioAnticipo guardar(RecordatorioAnticipo recordatorio);

    Optional<RecordatorioAnticipo> buscarPorId(UUID id);

    boolean existePendientePorEventoYFecha(UUID eventoId, LocalDate fechaRecordatorio);

    List<RecordatorioAnticipo> buscarPendientesHasta(LocalDate fechaReferencia, int limite);
}
