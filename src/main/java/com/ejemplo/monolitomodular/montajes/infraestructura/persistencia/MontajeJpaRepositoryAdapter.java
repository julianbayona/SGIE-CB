package com.ejemplo.monolitomodular.montajes.infraestructura.persistencia;

import com.ejemplo.monolitomodular.montajes.dominio.modelo.AdicionalEvento;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.InfraestructuraReserva;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.Montaje;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.MontajeMesaReserva;
import com.ejemplo.monolitomodular.montajes.dominio.puerto.salida.MontajeRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MontajeJpaRepositoryAdapter implements MontajeRepository {

    private final SpringDataMontajeJpaRepository montajeRepository;
    private final SpringDataMontajeMesaReservaJpaRepository mesasRepository;
    private final SpringDataInfraestructuraReservaJpaRepository infraestructuraRepository;
    private final SpringDataInfraestructuraReservaItemJpaRepository infraestructuraItemRepository;
    private final SpringDataAdicionalEventoJpaRepository adicionalRepository;

    public MontajeJpaRepositoryAdapter(
            SpringDataMontajeJpaRepository montajeRepository,
            SpringDataMontajeMesaReservaJpaRepository mesasRepository,
            SpringDataInfraestructuraReservaJpaRepository infraestructuraRepository,
            SpringDataInfraestructuraReservaItemJpaRepository infraestructuraItemRepository,
            SpringDataAdicionalEventoJpaRepository adicionalRepository
    ) {
        this.montajeRepository = montajeRepository;
        this.mesasRepository = mesasRepository;
        this.infraestructuraRepository = infraestructuraRepository;
        this.infraestructuraItemRepository = infraestructuraItemRepository;
        this.adicionalRepository = adicionalRepository;
    }

    @Override
    public Montaje guardar(Montaje montaje) {
        LocalDateTime now = LocalDateTime.now();
        montajeRepository.save(new MontajeJpaEntity(
                montaje.getId(),
                montaje.getReservaId(),
                montaje.getObservaciones(),
                now,
                now
        ));

        mesasRepository.saveAll(montaje.getMesas().stream().map(this::toEntity).toList());
        InfraestructuraReservaJpaEntity infraestructuraEntity = infraestructuraRepository.save(toEntity(montaje.getInfraestructura()));
        infraestructuraItemRepository.deleteByInfraestructuraReservaId(infraestructuraEntity.getId());
        infraestructuraItemRepository.saveAll(toItemEntities(montaje.getInfraestructura(), infraestructuraEntity.getId(), now));
        adicionalRepository.saveAll(montaje.getAdicionales().stream().map(this::toEntity).toList());

        return buscarPorReservaId(montaje.getReservaId())
                .orElseThrow(() -> new DomainException("No fue posible guardar el montaje"));
    }

    @Override
    public Optional<Montaje> buscarPorReservaId(UUID reservaId) {
        return montajeRepository.findByReservaId(reservaId).map(this::toDomain);
    }

    private Montaje toDomain(MontajeJpaEntity montajeEntity) {
        List<MontajeMesaReserva> mesas = mesasRepository.findByMontajeId(montajeEntity.getId()).stream()
                .map(this::toDomain)
                .toList();
        InfraestructuraReserva infraestructura = infraestructuraRepository.findByMontajeId(montajeEntity.getId())
                .map(this::toDomain)
                .orElseThrow(() -> new DomainException("Infraestructura no encontrada para el montaje"));
        List<AdicionalEvento> adicionales = adicionalRepository.findByMontajeId(montajeEntity.getId()).stream()
                .map(this::toDomain)
                .toList();

        return Montaje.reconstruir(
                montajeEntity.getId(),
                montajeEntity.getReservaId(),
                montajeEntity.getObservaciones(),
                mesas,
                infraestructura,
                adicionales
        );
    }

    private MontajeMesaReservaJpaEntity toEntity(MontajeMesaReserva mesa) {
        return new MontajeMesaReservaJpaEntity(
                mesa.getId(),
                mesa.getMontajeId(),
                mesa.getTipoMesaId(),
                mesa.getTipoSillaId(),
                mesa.getSillaPorMesa(),
                mesa.getCantidadMesas(),
                mesa.getMantelId(),
                mesa.getSobremantelId(),
                mesa.isVajilla(),
                mesa.isFajon()
        );
    }

    private InfraestructuraReservaJpaEntity toEntity(InfraestructuraReserva infraestructura) {
        return new InfraestructuraReservaJpaEntity(
                infraestructura.getId(),
                infraestructura.getMontajeId(),
                infraestructura.isMesaPonque(),
                infraestructura.isMesaRegalos(),
                infraestructura.isEspacioMusicos(),
                infraestructura.isEstanteBombas()
        );
    }

    private AdicionalEventoJpaEntity toEntity(AdicionalEvento adicional) {
        return new AdicionalEventoJpaEntity(
                adicional.getId(),
                adicional.getMontajeId(),
                adicional.getTipoAdicionalId(),
                adicional.getCantidad()
        );
    }

    private MontajeMesaReserva toDomain(MontajeMesaReservaJpaEntity entity) {
        return MontajeMesaReserva.reconstruir(
                entity.getId(),
                entity.getMontajeId(),
                entity.getTipoMesaId(),
                entity.getTipoSillaId(),
                entity.getSillaPorMesa(),
                entity.getCantidadMesas(),
                entity.getMantelId(),
                entity.getSobremantelId(),
                entity.isVajilla(),
                entity.isFajon()
        );
    }

    private InfraestructuraReserva toDomain(InfraestructuraReservaJpaEntity entity) {
        List<InfraestructuraReservaItemJpaEntity> items = infraestructuraItemRepository.findByInfraestructuraReservaId(entity.getId());
        if (!items.isEmpty()) {
            return InfraestructuraReserva.reconstruir(
                    entity.getId(),
                    entity.getMontajeId(),
                    tieneInfraestructura(items, "MESA_PONQUE"),
                    tieneInfraestructura(items, "MESA_REGALOS"),
                    tieneInfraestructura(items, "ESPACIO_MUSICOS"),
                    tieneInfraestructura(items, "ESTANTE_BOMBAS")
            );
        }
        return InfraestructuraReserva.reconstruir(
                entity.getId(),
                entity.getMontajeId(),
                entity.isMesaPonque(),
                entity.isMesaRegalos(),
                entity.isEspacioMusicos(),
                entity.isEstanteBombas()
        );
    }

    private List<InfraestructuraReservaItemJpaEntity> toItemEntities(
            InfraestructuraReserva infraestructura,
            UUID infraestructuraReservaId,
            LocalDateTime now
    ) {
        return List.of(
                toItemEntity(infraestructura, infraestructuraReservaId, "MESA_PONQUE", infraestructura.isMesaPonque(), now),
                toItemEntity(infraestructura, infraestructuraReservaId, "MESA_REGALOS", infraestructura.isMesaRegalos(), now),
                toItemEntity(infraestructura, infraestructuraReservaId, "ESPACIO_MUSICOS", infraestructura.isEspacioMusicos(), now),
                toItemEntity(infraestructura, infraestructuraReservaId, "ESTANTE_BOMBAS", infraestructura.isEstanteBombas(), now)
        ).stream().flatMap(Optional::stream).toList();
    }

    private Optional<InfraestructuraReservaItemJpaEntity> toItemEntity(
            InfraestructuraReserva infraestructura,
            UUID infraestructuraReservaId,
            String tipo,
            boolean incluido,
            LocalDateTime now
    ) {
        if (!incluido) {
            return Optional.empty();
        }
        return Optional.of(new InfraestructuraReservaItemJpaEntity(
                UUID.randomUUID(),
                infraestructuraReservaId,
                infraestructura.getMontajeId(),
                tipo,
                1,
                null,
                now
        ));
    }

    private boolean tieneInfraestructura(List<InfraestructuraReservaItemJpaEntity> items, String tipo) {
        return items.stream().anyMatch(item -> tipo.equals(item.getTipo()) && item.getCantidad() > 0);
    }

    private AdicionalEvento toDomain(AdicionalEventoJpaEntity entity) {
        return AdicionalEvento.reconstruir(
                entity.getId(),
                entity.getMontajeId(),
                entity.getTipoAdicionalId(),
                entity.getCantidad()
        );
    }
}
