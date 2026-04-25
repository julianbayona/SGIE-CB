package com.ejemplo.monolitomodular.eventos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.HistorialEstadoEvento;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.HistorialEstadoEventoRepository;
import org.springframework.stereotype.Repository;

@Repository
public class HistorialEstadoEventoJpaRepositoryAdapter implements HistorialEstadoEventoRepository {

    private final SpringDataHistorialEstadoEventoJpaRepository repository;

    public HistorialEstadoEventoJpaRepositoryAdapter(SpringDataHistorialEstadoEventoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public HistorialEstadoEvento guardar(HistorialEstadoEvento historialEstadoEvento) {
        HistorialEstadoEventoJpaEntity entity = new HistorialEstadoEventoJpaEntity(
                historialEstadoEvento.getId(),
                historialEstadoEvento.getEventoId(),
                historialEstadoEvento.getUsuarioId(),
                historialEstadoEvento.getEstadoAnterior(),
                historialEstadoEvento.getEstadoNuevo(),
                historialEstadoEvento.getObservacion(),
                historialEstadoEvento.getFechaCambio()
        );
        HistorialEstadoEventoJpaEntity saved = repository.save(entity);
        return HistorialEstadoEvento.reconstruir(
                saved.getId(),
                saved.getEventoId(),
                saved.getUsuarioId(),
                saved.getEstadoAnterior(),
                saved.getEstadoNuevo(),
                saved.getObservacion(),
                saved.getFechaCambio()
        );
    }
}
