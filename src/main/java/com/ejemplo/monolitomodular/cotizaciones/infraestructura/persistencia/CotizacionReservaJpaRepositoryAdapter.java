package com.ejemplo.monolitomodular.cotizaciones.infraestructura.persistencia;

import com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida.CotizacionReservaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public class CotizacionReservaJpaRepositoryAdapter implements CotizacionReservaRepository {

    private final SpringDataCotizacionReservaJpaRepository repository;

    public CotizacionReservaJpaRepositoryAdapter(SpringDataCotizacionReservaJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void asociarReservas(UUID cotizacionId, Collection<UUID> reservaIds) {
        LocalDateTime now = LocalDateTime.now();
        repository.saveAll(reservaIds.stream()
                .map(reservaId -> new CotizacionReservaJpaEntity(cotizacionId, reservaId, now))
                .toList());
    }

    @Override
    public List<UUID> listarReservaIdsPorCotizacionId(UUID cotizacionId) {
        return repository.findReservaIdsByCotizacionId(cotizacionId);
    }
}
