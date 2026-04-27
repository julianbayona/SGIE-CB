package com.ejemplo.monolitomodular.catalogos.aplicacion.servicio;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoView;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoComida;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoEvento;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoComidaRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoEventoRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CatalogoApplicationServiceTest {

    @Test
    void deberiaCrearYDesactivarTipoEvento() {
        TipoEventoRepositoryStub tipoEventoRepository = new TipoEventoRepositoryStub();
        CatalogoApplicationService service = new CatalogoApplicationService(
                tipoEventoRepository,
                new TipoComidaRepositoryStub()
        );

        CatalogoBasicoView creado = service.crearTipoEvento(new CatalogoBasicoCommand("Boda", "Evento social"));
        CatalogoBasicoView desactivado = service.desactivarTipoEvento(creado.id());

        assertEquals("Boda", creado.nombre());
        assertFalse(desactivado.activo());
        assertFalse(tipoEventoRepository.existeActivoPorId(creado.id()));
    }

    @Test
    void noDeberiaPermitirTipoComidaDuplicado() {
        CatalogoApplicationService service = new CatalogoApplicationService(
                new TipoEventoRepositoryStub(),
                new TipoComidaRepositoryStub()
        );
        service.crearTipoComida(new CatalogoBasicoCommand("Cena", "Servicio nocturno"));

        assertThrows(
                DomainException.class,
                () -> service.crearTipoComida(new CatalogoBasicoCommand("cena", "Duplicado"))
        );
    }

    private static class TipoEventoRepositoryStub implements TipoEventoRepository {

        private final List<TipoEvento> tiposEvento = new ArrayList<>();

        @Override
        public TipoEvento guardar(TipoEvento tipoEvento) {
            tiposEvento.removeIf(actual -> actual.getId().equals(tipoEvento.getId()));
            tiposEvento.add(tipoEvento);
            return tipoEvento;
        }

        @Override
        public Optional<TipoEvento> buscarPorId(UUID id) {
            return tiposEvento.stream().filter(tipoEvento -> tipoEvento.getId().equals(id)).findFirst();
        }

        @Override
        public List<TipoEvento> listar() {
            return List.copyOf(tiposEvento);
        }

        @Override
        public boolean existeActivoPorId(UUID id) {
            return tiposEvento.stream().anyMatch(tipoEvento -> tipoEvento.getId().equals(id) && tipoEvento.isActivo());
        }

        @Override
        public boolean existePorNombre(String nombre) {
            return tiposEvento.stream().anyMatch(tipoEvento -> tipoEvento.getNombre().equalsIgnoreCase(nombre));
        }
    }

    private static class TipoComidaRepositoryStub implements TipoComidaRepository {

        private final List<TipoComida> tiposComida = new ArrayList<>();

        @Override
        public TipoComida guardar(TipoComida tipoComida) {
            tiposComida.removeIf(actual -> actual.getId().equals(tipoComida.getId()));
            tiposComida.add(tipoComida);
            return tipoComida;
        }

        @Override
        public Optional<TipoComida> buscarPorId(UUID id) {
            return tiposComida.stream().filter(tipoComida -> tipoComida.getId().equals(id)).findFirst();
        }

        @Override
        public List<TipoComida> listar() {
            return List.copyOf(tiposComida);
        }

        @Override
        public boolean existeActivoPorId(UUID id) {
            return tiposComida.stream().anyMatch(tipoComida -> tipoComida.getId().equals(id) && tipoComida.isActivo());
        }

        @Override
        public boolean existePorNombre(String nombre) {
            return tiposComida.stream().anyMatch(tipoComida -> tipoComida.getNombre().equalsIgnoreCase(nombre));
        }
    }
}
