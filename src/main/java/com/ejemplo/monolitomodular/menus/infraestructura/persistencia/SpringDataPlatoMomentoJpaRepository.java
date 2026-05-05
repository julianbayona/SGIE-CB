package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface SpringDataPlatoMomentoJpaRepository extends JpaRepository<PlatoMomentoJpaEntity, PlatoMomentoJpaId> {

    @Query("""
            select case when count(pm) > 0 then true else false end
            from PlatoMomentoJpaEntity pm, PlatoJpaEntity p
            where pm.platoId = :platoId
              and pm.tipoMomentoId = :tipoMomentoId
              and p.id = pm.platoId
              and p.activo = true
            """)
    boolean existsActivoByPlatoIdAndTipoMomentoId(UUID platoId, UUID tipoMomentoId);

    boolean existsByPlatoIdAndTipoMomentoId(UUID platoId, UUID tipoMomentoId);

    List<PlatoMomentoJpaEntity> findAllByOrderByPlatoIdAscTipoMomentoIdAsc();

    List<PlatoMomentoJpaEntity> findByPlatoIdOrderByTipoMomentoIdAsc(UUID platoId);

    List<PlatoMomentoJpaEntity> findByTipoMomentoIdOrderByPlatoIdAsc(UUID tipoMomentoId);

    void deleteByPlatoIdAndTipoMomentoId(UUID platoId, UUID tipoMomentoId);
}
