package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
