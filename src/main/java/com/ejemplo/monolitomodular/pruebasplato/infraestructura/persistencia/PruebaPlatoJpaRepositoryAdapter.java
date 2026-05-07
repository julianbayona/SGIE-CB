package com.ejemplo.monolitomodular.pruebasplato.infraestructura.persistencia;

import com.ejemplo.monolitomodular.pruebasplato.dominio.modelo.PruebaPlato;
import com.ejemplo.monolitomodular.pruebasplato.dominio.puerto.salida.PruebaPlatoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.ejemplo.monolitomodular.pruebasplato.dominio.modelo.EstadoPruebaPlato;

@Repository
public class PruebaPlatoJpaRepositoryAdapter implements PruebaPlatoRepository {

    private final SpringDataPruebaPlatoJpaRepository repository;

    public PruebaPlatoJpaRepositoryAdapter(SpringDataPruebaPlatoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public PruebaPlato guardar(PruebaPlato pruebaPlato) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = repository.findById(pruebaPlato.getId())
                .map(PruebaPlatoJpaEntity::getCreatedAt)
                .orElse(now);
        return toDomain(repository.save(new PruebaPlatoJpaEntity(
                pruebaPlato.getId(),
                pruebaPlato.getEventoId(),
                pruebaPlato.getFechaRealizacion(),
                pruebaPlato.getEstado(),
                createdAt,
                now
        )));
    }

    @Override
    public List<PruebaPlato> buscarProgramadasPorEventoId(UUID eventoId) {
        return repository.findByEventoIdAndEstado(eventoId, EstadoPruebaPlato.PROGRAMADA).stream()
                .map(this::toDomain)
                .toList();
    }

    private PruebaPlato toDomain(PruebaPlatoJpaEntity entity) {
        return PruebaPlato.reconstruir(
                entity.getId(),
                entity.getEventoId(),
                entity.getFechaRealizacion(),
                entity.getEstado()
        );
    }
}
