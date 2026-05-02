package com.ejemplo.monolitomodular.cotizaciones.infraestructura.persistencia;

import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.Cotizacion;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.CotizacionItem;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.EstadoCotizacion;
import com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida.CotizacionRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CotizacionJpaRepositoryAdapter implements CotizacionRepository {

    private static final List<EstadoCotizacion> ESTADOS_INACTIVOS = List.of(
            EstadoCotizacion.RECHAZADA,
            EstadoCotizacion.DESACTUALIZADA
    );

    private final SpringDataCotizacionJpaRepository cotizacionRepository;
    private final SpringDataCotizacionItemJpaRepository itemRepository;

    public CotizacionJpaRepositoryAdapter(
            SpringDataCotizacionJpaRepository cotizacionRepository,
            SpringDataCotizacionItemJpaRepository itemRepository
    ) {
        this.cotizacionRepository = cotizacionRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Cotizacion guardar(Cotizacion cotizacion) {
        LocalDateTime now = LocalDateTime.now();
        cotizacionRepository.save(new CotizacionJpaEntity(
                cotizacion.getId(),
                cotizacion.getReservaId(),
                cotizacion.getUsuarioId(),
                cotizacion.getEstado(),
                cotizacion.getValorSubtotal(),
                cotizacion.getDescuento(),
                cotizacion.getValorTotal(),
                cotizacion.getObservaciones(),
                now,
                now
        ));
        itemRepository.saveAll(cotizacion.getItems().stream().map(this::toEntity).toList());
        return buscarPorId(cotizacion.getId()).orElseThrow();
    }

    @Override
    public Optional<Cotizacion> buscarPorId(UUID id) {
        return cotizacionRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Cotizacion> buscarActivaPorReservaId(UUID reservaId) {
        return cotizacionRepository.findByReservaIdAndEstadoNotInOrderByCreatedAtDesc(reservaId, ESTADOS_INACTIVOS)
                .stream()
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public Optional<Cotizacion> buscarUltimaPorReservaRaizId(UUID reservaRaizId) {
        return cotizacionRepository.findByReservaRaizIdOrderByCreatedAtDesc(reservaRaizId)
                .stream()
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public void desactualizarActivasPorReservaId(UUID reservaId) {
        cotizacionRepository.desactualizarActivasPorReservaId(reservaId, LocalDateTime.now());
    }

    private Cotizacion toDomain(CotizacionJpaEntity entity) {
        List<CotizacionItem> items = itemRepository.findByCotizacionId(entity.getId()).stream()
                .map(this::toDomain)
                .toList();
        return Cotizacion.reconstruir(
                entity.getId(),
                entity.getReservaId(),
                entity.getUsuarioId(),
                entity.getEstado(),
                entity.getDescuento(),
                entity.getObservaciones(),
                items
        );
    }

    private CotizacionItem toDomain(CotizacionItemJpaEntity entity) {
        return CotizacionItem.reconstruir(
                entity.getId(),
                entity.getCotizacionId(),
                entity.getTipoConcepto(),
                entity.getOrigenId(),
                entity.getDescripcion(),
                entity.getPrecioBase(),
                entity.getPrecioOverride(),
                entity.getCantidad()
        );
    }

    private CotizacionItemJpaEntity toEntity(CotizacionItem item) {
        return new CotizacionItemJpaEntity(
                item.getId(),
                item.getCotizacionId(),
                item.getTipoConcepto(),
                item.getOrigenId(),
                item.getDescripcion(),
                item.getPrecioBase(),
                item.getPrecioOverride(),
                item.getCantidad(),
                item.getSubtotal()
        );
    }
}
