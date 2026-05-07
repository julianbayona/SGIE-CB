package com.ejemplo.monolitomodular.pruebasplato.infraestructura.persistencia;

import com.ejemplo.monolitomodular.pruebasplato.dominio.modelo.EstadoPruebaPlato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataPruebaPlatoJpaRepository extends JpaRepository<PruebaPlatoJpaEntity, UUID> {

    List<PruebaPlatoJpaEntity> findByEventoIdAndEstado(UUID eventoId, EstadoPruebaPlato estado);
}
