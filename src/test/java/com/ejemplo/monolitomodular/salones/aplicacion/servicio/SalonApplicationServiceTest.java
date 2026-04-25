package com.ejemplo.monolitomodular.salones.aplicacion.servicio;

import com.ejemplo.monolitomodular.salones.aplicacion.dto.RegistrarSalonCommand;
import com.ejemplo.monolitomodular.salones.aplicacion.dto.SalonView;
import com.ejemplo.monolitomodular.salones.dominio.modelo.Salon;
import com.ejemplo.monolitomodular.salones.dominio.puerto.salida.SalonRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SalonApplicationServiceTest {

    @Test
    void deberiaRegistrarSalon() {
        SalonApplicationService service = new SalonApplicationService(new InMemorySalonRepositoryStub());

        SalonView salon = service.ejecutar(new RegistrarSalonCommand("Salon Republica", 120, "Principal"));

        assertEquals("Salon Republica", salon.nombre());
        assertEquals(120, salon.capacidad());
    }

    @Test
    void noDeberiaPermitirNombresDuplicados() {
        InMemorySalonRepositoryStub repository = new InMemorySalonRepositoryStub();
        SalonApplicationService service = new SalonApplicationService(repository);
        service.ejecutar(new RegistrarSalonCommand("Salon Republica", 120, "Principal"));

        assertThrows(
                DomainException.class,
                () -> service.ejecutar(new RegistrarSalonCommand("Salon Republica", 80, "Auxiliar"))
        );
    }

    private static class InMemorySalonRepositoryStub implements SalonRepository {

        private final List<Salon> salones = new ArrayList<>();

        @Override
        public Salon guardar(Salon salon) {
            salones.add(salon);
            return salon;
        }

        @Override
        public Optional<Salon> buscarPorId(UUID id) {
            return salones.stream().filter(salon -> salon.getId().equals(id)).findFirst();
        }

        @Override
        public List<Salon> listar() {
            return List.copyOf(salones);
        }

        @Override
        public List<Salon> buscarTodosPorIds(Collection<UUID> ids) {
            return salones.stream().filter(salon -> ids.contains(salon.getId())).toList();
        }

        @Override
        public boolean existePorNombre(String nombre) {
            return salones.stream().anyMatch(salon -> salon.getNombre().equalsIgnoreCase(nombre));
        }
    }
}
