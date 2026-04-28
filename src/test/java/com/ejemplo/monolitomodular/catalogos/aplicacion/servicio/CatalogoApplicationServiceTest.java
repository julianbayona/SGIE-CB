package com.ejemplo.monolitomodular.catalogos.aplicacion.servicio;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoConColorCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoConColorView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.ColorCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.TipoAdicionalCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.TipoAdicionalView;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Color;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Mantel;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.ModoCobroAdicional;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Sobremantel;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoAdicional;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoComida;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoEvento;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoMesa;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoSilla;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.ColorRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.MantelRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.SobremantelRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoAdicionalRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoComidaRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoEventoRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoMesaRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoSillaRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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
                new TipoComidaRepositoryStub(),
                new ColorRepositoryStub(),
                new TipoMesaRepositoryStub(),
                new TipoSillaRepositoryStub(),
                new MantelRepositoryStub(),
                new SobremantelRepositoryStub(),
                new TipoAdicionalRepositoryStub()
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
                new TipoComidaRepositoryStub(),
                new ColorRepositoryStub(),
                new TipoMesaRepositoryStub(),
                new TipoSillaRepositoryStub(),
                new MantelRepositoryStub(),
                new SobremantelRepositoryStub(),
                new TipoAdicionalRepositoryStub()
        );
        service.crearTipoComida(new CatalogoBasicoCommand("Cena", "Servicio nocturno"));

        assertThrows(
                DomainException.class,
                () -> service.crearTipoComida(new CatalogoBasicoCommand("cena", "Duplicado"))
        );
    }

    @Test
    void deberiaCrearMantelConColorActivo() {
        ColorRepositoryStub colorRepository = new ColorRepositoryStub();
        MantelRepositoryStub mantelRepository = new MantelRepositoryStub();
        CatalogoApplicationService service = new CatalogoApplicationService(
                new TipoEventoRepositoryStub(),
                new TipoComidaRepositoryStub(),
                colorRepository,
                new TipoMesaRepositoryStub(),
                new TipoSillaRepositoryStub(),
                mantelRepository,
                new SobremantelRepositoryStub(),
                new TipoAdicionalRepositoryStub()
        );

        var color = service.crearColor(new ColorCommand("Rojo vino", "#7B1E2B"));
        CatalogoConColorView mantel = service.crearMantel(new CatalogoConColorCommand("Mantel rojo vino", color.id()));

        assertEquals("Mantel rojo vino", mantel.nombre());
        assertEquals(color.id(), mantel.colorId());
    }

    @Test
    void noDeberiaCrearSobremantelConColorInactivo() {
        ColorRepositoryStub colorRepository = new ColorRepositoryStub();
        CatalogoApplicationService service = new CatalogoApplicationService(
                new TipoEventoRepositoryStub(),
                new TipoComidaRepositoryStub(),
                colorRepository,
                new TipoMesaRepositoryStub(),
                new TipoSillaRepositoryStub(),
                new MantelRepositoryStub(),
                new SobremantelRepositoryStub(),
                new TipoAdicionalRepositoryStub()
        );

        var color = service.crearColor(new ColorCommand("Azul", "#0033AA"));
        service.desactivarColor(color.id());

        assertThrows(
                DomainException.class,
                () -> service.crearSobremantel(new CatalogoConColorCommand("Sobremantel azul", color.id()))
        );
    }

    @Test
    void deberiaCrearYDesactivarTipoAdicional() {
        TipoAdicionalRepositoryStub tipoAdicionalRepository = new TipoAdicionalRepositoryStub();
        CatalogoApplicationService service = new CatalogoApplicationService(
                new TipoEventoRepositoryStub(),
                new TipoComidaRepositoryStub(),
                new ColorRepositoryStub(),
                new TipoMesaRepositoryStub(),
                new TipoSillaRepositoryStub(),
                new MantelRepositoryStub(),
                new SobremantelRepositoryStub(),
                tipoAdicionalRepository
        );

        TipoAdicionalView creado = service.crearTipoAdicional(
                new TipoAdicionalCommand("Video beam", ModoCobroAdicional.SERVICIO, new BigDecimal("120000.00"))
        );
        TipoAdicionalView desactivado = service.desactivarTipoAdicional(creado.id());

        assertEquals("Video beam", creado.nombre());
        assertEquals(new BigDecimal("120000.00"), creado.precioBase());
        assertFalse(desactivado.activo());
        assertFalse(tipoAdicionalRepository.existeActivoPorId(creado.id()));
    }

    @Test
    void deberiaCrearYDesactivarTipoMesa() {
        TipoMesaRepositoryStub tipoMesaRepository = new TipoMesaRepositoryStub();
        CatalogoApplicationService service = new CatalogoApplicationService(
                new TipoEventoRepositoryStub(),
                new TipoComidaRepositoryStub(),
                new ColorRepositoryStub(),
                tipoMesaRepository,
                new TipoSillaRepositoryStub(),
                new MantelRepositoryStub(),
                new SobremantelRepositoryStub(),
                new TipoAdicionalRepositoryStub()
        );

        CatalogoBasicoView creado = service.crearTipoMesa(new CatalogoBasicoCommand("Mesa redonda", null));
        CatalogoBasicoView desactivado = service.desactivarTipoMesa(creado.id());

        assertEquals("Mesa redonda", creado.nombre());
        assertFalse(desactivado.activo());
        assertFalse(tipoMesaRepository.existeActivoPorId(creado.id()));
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

    private static class ColorRepositoryStub implements ColorRepository {

        private final List<Color> colores = new ArrayList<>();

        @Override
        public Color guardar(Color color) {
            colores.removeIf(actual -> actual.getId().equals(color.getId()));
            colores.add(color);
            return color;
        }

        @Override
        public Optional<Color> buscarPorId(UUID id) {
            return colores.stream().filter(color -> color.getId().equals(id)).findFirst();
        }

        @Override
        public List<Color> listar() {
            return List.copyOf(colores);
        }

        @Override
        public boolean existeActivoPorId(UUID id) {
            return colores.stream().anyMatch(color -> color.getId().equals(id) && color.isActivo());
        }

        @Override
        public boolean existePorNombre(String nombre) {
            return colores.stream().anyMatch(color -> color.getNombre().equalsIgnoreCase(nombre));
        }
    }

    private static class TipoSillaRepositoryStub implements TipoSillaRepository {

        private final List<TipoSilla> tiposSilla = new ArrayList<>();

        @Override
        public TipoSilla guardar(TipoSilla tipoSilla) {
            tiposSilla.removeIf(actual -> actual.getId().equals(tipoSilla.getId()));
            tiposSilla.add(tipoSilla);
            return tipoSilla;
        }

        @Override
        public Optional<TipoSilla> buscarPorId(UUID id) {
            return tiposSilla.stream().filter(tipoSilla -> tipoSilla.getId().equals(id)).findFirst();
        }

        @Override
        public List<TipoSilla> listar() {
            return List.copyOf(tiposSilla);
        }

        @Override
        public boolean existeActivoPorId(UUID id) {
            return tiposSilla.stream().anyMatch(tipoSilla -> tipoSilla.getId().equals(id) && tipoSilla.isActivo());
        }

        @Override
        public boolean existePorNombre(String nombre) {
            return tiposSilla.stream().anyMatch(tipoSilla -> tipoSilla.getNombre().equalsIgnoreCase(nombre));
        }
    }

    private static class TipoMesaRepositoryStub implements TipoMesaRepository {

        private final List<TipoMesa> tiposMesa = new ArrayList<>();

        @Override
        public TipoMesa guardar(TipoMesa tipoMesa) {
            tiposMesa.removeIf(actual -> actual.getId().equals(tipoMesa.getId()));
            tiposMesa.add(tipoMesa);
            return tipoMesa;
        }

        @Override
        public Optional<TipoMesa> buscarPorId(UUID id) {
            return tiposMesa.stream().filter(tipoMesa -> tipoMesa.getId().equals(id)).findFirst();
        }

        @Override
        public List<TipoMesa> listar() {
            return List.copyOf(tiposMesa);
        }

        @Override
        public boolean existeActivoPorId(UUID id) {
            return tiposMesa.stream().anyMatch(tipoMesa -> tipoMesa.getId().equals(id) && tipoMesa.isActivo());
        }

        @Override
        public boolean existePorNombre(String nombre) {
            return tiposMesa.stream().anyMatch(tipoMesa -> tipoMesa.getNombre().equalsIgnoreCase(nombre));
        }
    }

    private static class MantelRepositoryStub implements MantelRepository {

        private final List<Mantel> manteles = new ArrayList<>();

        @Override
        public Mantel guardar(Mantel mantel) {
            manteles.removeIf(actual -> actual.getId().equals(mantel.getId()));
            manteles.add(mantel);
            return mantel;
        }

        @Override
        public Optional<Mantel> buscarPorId(UUID id) {
            return manteles.stream().filter(mantel -> mantel.getId().equals(id)).findFirst();
        }

        @Override
        public List<Mantel> listar() {
            return List.copyOf(manteles);
        }

        @Override
        public boolean existeActivoPorId(UUID id) {
            return manteles.stream().anyMatch(mantel -> mantel.getId().equals(id) && mantel.isActivo());
        }

        @Override
        public boolean existePorNombre(String nombre) {
            return manteles.stream().anyMatch(mantel -> mantel.getNombre().equalsIgnoreCase(nombre));
        }
    }

    private static class SobremantelRepositoryStub implements SobremantelRepository {

        private final List<Sobremantel> sobremanteles = new ArrayList<>();

        @Override
        public Sobremantel guardar(Sobremantel sobremantel) {
            sobremanteles.removeIf(actual -> actual.getId().equals(sobremantel.getId()));
            sobremanteles.add(sobremantel);
            return sobremantel;
        }

        @Override
        public Optional<Sobremantel> buscarPorId(UUID id) {
            return sobremanteles.stream().filter(sobremantel -> sobremantel.getId().equals(id)).findFirst();
        }

        @Override
        public List<Sobremantel> listar() {
            return List.copyOf(sobremanteles);
        }

        @Override
        public boolean existeActivoPorId(UUID id) {
            return sobremanteles.stream().anyMatch(sobremantel -> sobremantel.getId().equals(id) && sobremantel.isActivo());
        }

        @Override
        public boolean existePorNombre(String nombre) {
            return sobremanteles.stream().anyMatch(sobremantel -> sobremantel.getNombre().equalsIgnoreCase(nombre));
        }
    }

    private static class TipoAdicionalRepositoryStub implements TipoAdicionalRepository {

        private final List<TipoAdicional> tiposAdicional = new ArrayList<>();

        @Override
        public TipoAdicional guardar(TipoAdicional tipoAdicional) {
            tiposAdicional.removeIf(actual -> actual.getId().equals(tipoAdicional.getId()));
            tiposAdicional.add(tipoAdicional);
            return tipoAdicional;
        }

        @Override
        public Optional<TipoAdicional> buscarPorId(UUID id) {
            return tiposAdicional.stream().filter(tipoAdicional -> tipoAdicional.getId().equals(id)).findFirst();
        }

        @Override
        public List<TipoAdicional> listar() {
            return List.copyOf(tiposAdicional);
        }

        @Override
        public boolean existeActivoPorId(UUID id) {
            return tiposAdicional.stream().anyMatch(tipoAdicional -> tipoAdicional.getId().equals(id) && tipoAdicional.isActivo());
        }

        @Override
        public boolean existePorNombre(String nombre) {
            return tiposAdicional.stream().anyMatch(tipoAdicional -> tipoAdicional.getNombre().equalsIgnoreCase(nombre));
        }
    }
}
