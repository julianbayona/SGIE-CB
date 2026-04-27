package com.ejemplo.monolitomodular.clientes.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataClienteJpaRepository extends JpaRepository<ClienteJpaEntity, UUID> {

    Optional<ClienteJpaEntity> findByCedulaIgnoreCase(String cedula);

    @Query("""
            select c
            from ClienteJpaEntity c
            where lower(c.cedula) like lower(concat('%', :filtro, '%'))
               or lower(c.nombreCompleto) like lower(concat('%', :filtro, '%'))
               or lower(c.telefono) like lower(concat('%', :filtro, '%'))
            order by c.nombreCompleto asc
            """)
    List<ClienteJpaEntity> buscarPorFiltro(String filtro);
}
