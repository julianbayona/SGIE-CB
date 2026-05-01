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

  
    
    @Test
    void deberiaActualizarTipoEvento() {
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

        CatalogoBasicoView creado = service.crearTipoEvento(new CatalogoBasicoCommand("Boda", "Evento formal"));
        CatalogoBasicoView actualizado = service.actualizarTipoEvento(creado.id(), new CatalogoBasicoCommand("Matrimonio", "Evento social"));

        assertEquals("Matrimonio", actualizado.nombre());
        assertEquals("Evento social", actualizado.descripcion());
    }

    @Test
    void noDeberiaActualizarTipoEventoNoExistente() {
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

        assertThrows(DomainException.class, () -> 
                service.actualizarTipoEvento(UUID.randomUUID(), new CatalogoBasicoCommand("Boda", "Nueva"))
        );
    }

    @Test
    void noDeberiaActualizarTipoEventoConNombreDuplicado() {
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

        CatalogoBasicoView evento1 = service.crearTipoEvento(new CatalogoBasicoCommand("Boda", ""));
        CatalogoBasicoView evento2 = service.crearTipoEvento(new CatalogoBasicoCommand("Conferencia", ""));

        assertThrows(DomainException.class, () ->
                service.actualizarTipoEvento(evento2.id(), new CatalogoBasicoCommand("Boda", ""))
        );
    }

    @Test
    void deberiaObtenerTipoEventoPorId() {
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

        CatalogoBasicoView creado = service.crearTipoEvento(new CatalogoBasicoCommand("Boda", "Evento formal"));
        CatalogoBasicoView obtenido = service.obtenerTipoEvento(creado.id());

        assertEquals("Boda", obtenido.nombre());
        assertEquals(creado.id(), obtenido.id());
    }

    @Test
    void noDeberiaObtenerTipoEventoNoExistente() {
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

        assertThrows(DomainException.class, () -> service.obtenerTipoEvento(UUID.randomUUID()));
    }

    @Test
    void deberiaListarTiposEventoVacio() {
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

        assertEquals(0, service.listarTiposEvento().size());
    }

    @Test
    void deberiaListarMultiplesTiposEvento() {
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

        service.crearTipoEvento(new CatalogoBasicoCommand("Boda", ""));
        service.crearTipoEvento(new CatalogoBasicoCommand("Conferencia", ""));
        service.crearTipoEvento(new CatalogoBasicoCommand("Fiesta", ""));

        assertEquals(3, service.listarTiposEvento().size());
    }

    // --- Gestión de TipoComida ---
    
    @Test
    void deberiaActualizarTipoComida() {
        TipoComidaRepositoryStub tipoComidaRepository = new TipoComidaRepositoryStub();
        CatalogoApplicationService service = new CatalogoApplicationService(
                new TipoEventoRepositoryStub(),
                tipoComidaRepository,
                new ColorRepositoryStub(),
                new TipoMesaRepositoryStub(),
                new TipoSillaRepositoryStub(),
                new MantelRepositoryStub(),
                new SobremantelRepositoryStub(),
                new TipoAdicionalRepositoryStub()
        );

        CatalogoBasicoView creado = service.crearTipoComida(new CatalogoBasicoCommand("Almuerzo", "Servicio diurno"));
        CatalogoBasicoView actualizado = service.actualizarTipoComida(creado.id(), new CatalogoBasicoCommand("Comida", "Servicio medio día"));

        assertEquals("Comida", actualizado.nombre());
    }

    @Test
    void noDeberiaActualizarTipoComidaConNombreDuplicado() {
        TipoComidaRepositoryStub tipoComidaRepository = new TipoComidaRepositoryStub();
        CatalogoApplicationService service = new CatalogoApplicationService(
                new TipoEventoRepositoryStub(),
                tipoComidaRepository,
                new ColorRepositoryStub(),
                new TipoMesaRepositoryStub(),
                new TipoSillaRepositoryStub(),
                new MantelRepositoryStub(),
                new SobremantelRepositoryStub(),
                new TipoAdicionalRepositoryStub()
        );

        service.crearTipoComida(new CatalogoBasicoCommand("Desayuno", ""));
        CatalogoBasicoView comida2 = service.crearTipoComida(new CatalogoBasicoCommand("Almuerzo", ""));

        assertThrows(DomainException.class, () ->
                service.actualizarTipoComida(comida2.id(), new CatalogoBasicoCommand("Desayuno", ""))
        );
    }

    @Test
    void deberiaDesactivarTipoComida() {
        TipoComidaRepositoryStub tipoComidaRepository = new TipoComidaRepositoryStub();
        CatalogoApplicationService service = new CatalogoApplicationService(
                new TipoEventoRepositoryStub(),
                tipoComidaRepository,
                new ColorRepositoryStub(),
                new TipoMesaRepositoryStub(),
                new TipoSillaRepositoryStub(),
                new MantelRepositoryStub(),
                new SobremantelRepositoryStub(),
                new TipoAdicionalRepositoryStub()
        );

        CatalogoBasicoView creado = service.crearTipoComida(new CatalogoBasicoCommand("Cena", "Servicio nocturno"));
        CatalogoBasicoView desactivado = service.desactivarTipoComida(creado.id());

        assertFalse(desactivado.activo());
    }

    @Test
    void deberiaObtenerTipoComidaPorId() {
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

        CatalogoBasicoView creado = service.crearTipoComida(new CatalogoBasicoCommand("Almuerzo", ""));
        CatalogoBasicoView obtenido = service.obtenerTipoComida(creado.id());

        assertEquals(creado.id(), obtenido.id());
        assertEquals("Almuerzo", obtenido.nombre());
    }

    @Test
    void deberiaListarTiposComida() {
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

        service.crearTipoComida(new CatalogoBasicoCommand("Desayuno", ""));
        service.crearTipoComida(new CatalogoBasicoCommand("Almuerzo", ""));

        assertEquals(2, service.listarTiposComida().size());
    }

    // --- Gestión de Colores ---
    
    @Test
    void deberiaCrearColor() {
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

        var color = service.crearColor(new ColorCommand("Rojo", "#FF0000"));

        assertEquals("Rojo", color.nombre());
        assertEquals("#FF0000", color.codigoHex());
    }

    @Test
    void noDeberiaCrearColorDuplicado() {
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

        service.crearColor(new ColorCommand("Azul", "#0000FF"));

        assertThrows(DomainException.class, () ->
                service.crearColor(new ColorCommand("azul", "#0000AA"))
        );
    }

    @Test
    void deberiaActualizarColor() {
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

        var color = service.crearColor(new ColorCommand("Verde", "#00FF00"));
        var actualizado = service.actualizarColor(color.id(), new ColorCommand("Verde Claro", "#90EE90"));

        assertEquals("Verde Claro", actualizado.nombre());
        assertEquals("#90EE90", actualizado.codigoHex());
    }

    @Test
    void deberiaDesactivarColor() {
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

        var color = service.crearColor(new ColorCommand("Amarillo", "#FFFF00"));
        var desactivado = service.desactivarColor(color.id());

        assertFalse(desactivado.activo());
    }

    @Test
    void deberiaObtenerColorPorId() {
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

        var color = service.crearColor(new ColorCommand("Negro", "#000000"));
        var obtenido = service.obtenerColor(color.id());

        assertEquals(color.id(), obtenido.id());
        assertEquals("Negro", obtenido.nombre());
    }

    @Test
    void deberiaListarColores() {
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

        service.crearColor(new ColorCommand("Blanco", "#FFFFFF"));
        service.crearColor(new ColorCommand("Gris", "#808080"));

        assertEquals(2, service.listarColores().size());
    }

    // --- Gestión de TipoMesa ---
    
    @Test
    void deberiaActualizarTipoMesa() {
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

        CatalogoBasicoView creado = service.crearTipoMesa(new CatalogoBasicoCommand("Mesa rectangular", null));
        CatalogoBasicoView actualizado = service.actualizarTipoMesa(creado.id(), new CatalogoBasicoCommand("Mesa cuadrada", null));

        assertEquals("Mesa cuadrada", actualizado.nombre());
    }

    @Test
    void noDeberiaCrearTipoMesaDuplicado() {
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

        service.crearTipoMesa(new CatalogoBasicoCommand("Mesa Alta", null));

        assertThrows(DomainException.class, () ->
                service.crearTipoMesa(new CatalogoBasicoCommand("mesa alta", null))
        );
    }

    @Test
    void deberiaObtenerTipoMesaPorId() {
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

        CatalogoBasicoView creado = service.crearTipoMesa(new CatalogoBasicoCommand("Mesa plegable", null));
        CatalogoBasicoView obtenido = service.obtenerTipoMesa(creado.id());

        assertEquals("Mesa plegable", obtenido.nombre());
    }

    @Test
    void deberiaListarTiposMesa() {
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

        service.crearTipoMesa(new CatalogoBasicoCommand("Mesa 1", null));
        service.crearTipoMesa(new CatalogoBasicoCommand("Mesa 2", null));

        assertEquals(2, service.listarTiposMesa().size());
    }

    // --- Gestión de TipoSilla ---
    
    @Test
    void deberiaCrearYDesactivarTipoSilla() {
        TipoSillaRepositoryStub tipoSillaRepository = new TipoSillaRepositoryStub();
        CatalogoApplicationService service = new CatalogoApplicationService(
                new TipoEventoRepositoryStub(),
                new TipoComidaRepositoryStub(),
                new ColorRepositoryStub(),
                new TipoMesaRepositoryStub(),
                tipoSillaRepository,
                new MantelRepositoryStub(),
                new SobremantelRepositoryStub(),
                new TipoAdicionalRepositoryStub()
        );

        CatalogoBasicoView creado = service.crearTipoSilla(new CatalogoBasicoCommand("Silla moderna", null));
        CatalogoBasicoView desactivado = service.desactivarTipoSilla(creado.id());

        assertEquals("Silla moderna", creado.nombre());
        assertFalse(desactivado.activo());
    }

    @Test
    void deberiaActualizarTipoSilla() {
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

        CatalogoBasicoView creado = service.crearTipoSilla(new CatalogoBasicoCommand("Silla de madera", null));
        CatalogoBasicoView actualizado = service.actualizarTipoSilla(creado.id(), new CatalogoBasicoCommand("Silla de metal", null));

        assertEquals("Silla de metal", actualizado.nombre());
    }

    @Test
    void noDeberiaCrearTipoSillaDuplicado() {
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

        service.crearTipoSilla(new CatalogoBasicoCommand("Silla plastica", null));

        assertThrows(DomainException.class, () ->
                service.crearTipoSilla(new CatalogoBasicoCommand("SILLA PLASTICA", null))
        );
    }

    @Test
    void deberiaObtenerTipoSillaPorId() {
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

        CatalogoBasicoView creado = service.crearTipoSilla(new CatalogoBasicoCommand("Silla", null));
        CatalogoBasicoView obtenido = service.obtenerTipoSilla(creado.id());

        assertEquals(creado.id(), obtenido.id());
    }

    @Test
    void deberiaListarTiposSilla() {
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

        service.crearTipoSilla(new CatalogoBasicoCommand("Silla 1", null));
        service.crearTipoSilla(new CatalogoBasicoCommand("Silla 2", null));

        assertEquals(2, service.listarTiposSilla().size());
    }

    // --- Gestión de Manteles ---
    
    @Test
    void deberiaActualizarMantel() {
        MantelRepositoryStub mantelRepository = new MantelRepositoryStub();
        ColorRepositoryStub colorRepository = new ColorRepositoryStub();
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

        var color1 = service.crearColor(new ColorCommand("Rojo", "#FF0000"));
        var color2 = service.crearColor(new ColorCommand("Azul", "#0000FF"));
        var mantel = service.crearMantel(new CatalogoConColorCommand("Mantel rojo", color1.id()));
        var actualizado = service.actualizarMantel(mantel.id(), new CatalogoConColorCommand("Mantel azul", color2.id()));

        assertEquals("Mantel azul", actualizado.nombre());
        assertEquals(color2.id(), actualizado.colorId());
    }

    @Test
    void noDeberiaCrearMantelConColorInactivo() {
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

        var color = service.crearColor(new ColorCommand("Verde", "#00FF00"));
        service.desactivarColor(color.id());

        assertThrows(DomainException.class, () ->
                service.crearMantel(new CatalogoConColorCommand("Mantel verde", color.id()))
        );
    }

    @Test
    void noDeberiaCrearMantelDuplicado() {
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

        var color = service.crearColor(new ColorCommand("Blanco", "#FFFFFF"));
        service.crearMantel(new CatalogoConColorCommand("Mantel blanco", color.id()));

        assertThrows(DomainException.class, () ->
                service.crearMantel(new CatalogoConColorCommand("mantel blanco", color.id()))
        );
    }

    @Test
    void deberiaDesactivarMantel() {
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

        var color = service.crearColor(new ColorCommand("Negro", "#000000"));
        var mantel = service.crearMantel(new CatalogoConColorCommand("Mantel negro", color.id()));
        var desactivado = service.desactivarMantel(mantel.id());

        assertFalse(desactivado.activo());
    }

    @Test
    void deberiaObtenerMantelPorId() {
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

        var color = service.crearColor(new ColorCommand("Gris", "#808080"));
        var mantel = service.crearMantel(new CatalogoConColorCommand("Mantel gris", color.id()));
        var obtenido = service.obtenerMantel(mantel.id());

        assertEquals("Mantel gris", obtenido.nombre());
    }

    @Test
    void deberiaListarManteles() {
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

        var color = service.crearColor(new ColorCommand("Amarillo", "#FFFF00"));
        service.crearMantel(new CatalogoConColorCommand("Mantel 1", color.id()));
        service.crearMantel(new CatalogoConColorCommand("Mantel 2", color.id()));

        assertEquals(2, service.listarManteles().size());
    }

    // --- Gestión de Sobremanteles ---
    
    @Test
    void deberiaCrearSobremantel() {
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

        var color = service.crearColor(new ColorCommand("Rosa", "#FFC0CB"));
        var sobremantel = service.crearSobremantel(new CatalogoConColorCommand("Sobremantel rosa", color.id()));

        assertEquals("Sobremantel rosa", sobremantel.nombre());
        assertEquals(color.id(), sobremantel.colorId());
    }

    @Test
    void deberiaActualizarSobremantel() {
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

        var color1 = service.crearColor(new ColorCommand("Naranja", "#FFA500"));
        var color2 = service.crearColor(new ColorCommand("Morado", "#800080"));
        var sobremantel = service.crearSobremantel(new CatalogoConColorCommand("Sobremantel naranja", color1.id()));
        var actualizado = service.actualizarSobremantel(sobremantel.id(), new CatalogoConColorCommand("Sobremantel morado", color2.id()));

        assertEquals("Sobremantel morado", actualizado.nombre());
    }

    @Test
    void noDeberiaCrearSobremantelDuplicado() {
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

        var color = service.crearColor(new ColorCommand("Turquesa", "#40E0D0"));
        service.crearSobremantel(new CatalogoConColorCommand("Sobremantel turquesa", color.id()));

        assertThrows(DomainException.class, () ->
                service.crearSobremantel(new CatalogoConColorCommand("sobremantel turquesa", color.id()))
        );
    }

    @Test
    void deberiaDesactivarSobremantel() {
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

        var color = service.crearColor(new ColorCommand("Cian", "#00FFFF"));
        var sobremantel = service.crearSobremantel(new CatalogoConColorCommand("Sobremantel cian", color.id()));
        var desactivado = service.desactivarSobremantel(sobremantel.id());

        assertFalse(desactivado.activo());
    }

    @Test
    void deberiaObtenerSobremantelPorId() {
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

        var color = service.crearColor(new ColorCommand("Magenta", "#FF00FF"));
        var sobremantel = service.crearSobremantel(new CatalogoConColorCommand("Sobremantel magenta", color.id()));
        var obtenido = service.obtenerSobremantel(sobremantel.id());

        assertEquals("Sobremantel magenta", obtenido.nombre());
    }

    @Test
    void deberiaListarSobremanteles() {
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

        var color = service.crearColor(new ColorCommand("Beige", "#F5F5DC"));
        service.crearSobremantel(new CatalogoConColorCommand("Sobremantel 1", color.id()));
        service.crearSobremantel(new CatalogoConColorCommand("Sobremantel 2", color.id()));

        assertEquals(2, service.listarSobremanteles().size());
    }

    // --- Gestión de TipoAdicional ---
    
    @Test
    void deberiaActualizarTipoAdicional() {
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

        var adicional = service.crearTipoAdicional(
                new TipoAdicionalCommand("Decoracion", ModoCobroAdicional.SERVICIO, new BigDecimal("50000.00"))
        );
        var actualizado = service.actualizarTipoAdicional(
                adicional.id(),
                new TipoAdicionalCommand("Decoracion Premium", ModoCobroAdicional.UNIDAD, new BigDecimal("75000.00"))
        );

        assertEquals("Decoracion Premium", actualizado.nombre());
        assertEquals(new BigDecimal("75000.00"), actualizado.precioBase());
    }

    @Test
    void noDeberiaCrearTipoAdicionalDuplicado() {
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

        service.crearTipoAdicional(
                new TipoAdicionalCommand("Musica", ModoCobroAdicional.SERVICIO, new BigDecimal("100000.00"))
        );

        assertThrows(DomainException.class, () ->
                service.crearTipoAdicional(
                        new TipoAdicionalCommand("musica", ModoCobroAdicional.UNIDAD, new BigDecimal("50000.00"))
                )
        );
    }

    @Test
    void deberiaObtenerTipoAdicionalPorId() {
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

        var adicional = service.crearTipoAdicional(
                new TipoAdicionalCommand("Fotografia", ModoCobroAdicional.SERVICIO, new BigDecimal("200000.00"))
        );
        var obtenido = service.obtenerTipoAdicional(adicional.id());

        assertEquals("Fotografia", obtenido.nombre());
        assertEquals(new BigDecimal("200000.00"), obtenido.precioBase());
    }

    @Test
    void deberiaListarTiposAdicional() {
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

        service.crearTipoAdicional(
                new TipoAdicionalCommand("Servicio 1", ModoCobroAdicional.SERVICIO, new BigDecimal("10000.00"))
        );
        service.crearTipoAdicional(
                new TipoAdicionalCommand("Servicio 2", ModoCobroAdicional.UNIDAD, new BigDecimal("20000.00"))
        );

        assertEquals(2, service.listarTiposAdicional().size());
    }

    // --- Casos de error adicionales para mayor cobertura ---
    
    @Test
    void noDeberiaDesactivarTipoComidaNoExistente() {
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

        assertThrows(DomainException.class, () -> service.desactivarTipoComida(UUID.randomUUID()));
    }

    @Test
    void noDeberiaObtenerTipoComidaNoExistente() {
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

        assertThrows(DomainException.class, () -> service.obtenerTipoComida(UUID.randomUUID()));
    }

    @Test
    void noDeberiaActualizarTipoComidaNoExistente() {
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

        assertThrows(DomainException.class, () -> 
                service.actualizarTipoComida(UUID.randomUUID(), new CatalogoBasicoCommand("Nueva", ""))
        );
    }

    @Test
    void noDeberiaDesactivarColorNoExistente() {
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

        assertThrows(DomainException.class, () -> service.desactivarColor(UUID.randomUUID()));
    }

    @Test
    void noDeberiaObtenerColorNoExistente() {
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

        assertThrows(DomainException.class, () -> service.obtenerColor(UUID.randomUUID()));
    }

    @Test
    void noDeberiaActualizarColorNoExistente() {
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

        assertThrows(DomainException.class, () -> 
                service.actualizarColor(UUID.randomUUID(), new ColorCommand("Nuevo", "#AAAAAA"))
        );
    }

    @Test
    void noDeberiaDesactivarTipoMesaNoExistente() {
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

        assertThrows(DomainException.class, () -> service.desactivarTipoMesa(UUID.randomUUID()));
    }

    @Test
    void noDeberiaObtenerTipoMesaNoExistente() {
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

        assertThrows(DomainException.class, () -> service.obtenerTipoMesa(UUID.randomUUID()));
    }

    @Test
    void noDeberiaActualizarTipoMesaNoExistente() {
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

        assertThrows(DomainException.class, () -> 
                service.actualizarTipoMesa(UUID.randomUUID(), new CatalogoBasicoCommand("Nueva", null))
        );
    }

    @Test
    void noDeberiaDesactivarTipoSillaNoExistente() {
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

        assertThrows(DomainException.class, () -> service.desactivarTipoSilla(UUID.randomUUID()));
    }

    @Test
    void noDeberiaObtenerTipoSillaNoExistente() {
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

        assertThrows(DomainException.class, () -> service.obtenerTipoSilla(UUID.randomUUID()));
    }

    @Test
    void noDeberiaActualizarTipoSillaNoExistente() {
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

        assertThrows(DomainException.class, () -> 
                service.actualizarTipoSilla(UUID.randomUUID(), new CatalogoBasicoCommand("Nueva", null))
        );
    }

    @Test
    void noDeberiaDesactivarMantelNoExistente() {
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

        assertThrows(DomainException.class, () -> service.desactivarMantel(UUID.randomUUID()));
    }

    @Test
    void noDeberiaObtenerMantelNoExistente() {
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

        assertThrows(DomainException.class, () -> service.obtenerMantel(UUID.randomUUID()));
    }

    @Test
    void noDeberiaActualizarMantelNoExistente() {
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

        var color = service.crearColor(new ColorCommand("Rojo", "#FF0000"));
        assertThrows(DomainException.class, () -> 
                service.actualizarMantel(UUID.randomUUID(), new CatalogoConColorCommand("Nuevo", color.id()))
        );
    }

    @Test
    void noDeberiaDesactivarSobremantelNoExistente() {
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

        assertThrows(DomainException.class, () -> service.desactivarSobremantel(UUID.randomUUID()));
    }

    @Test
    void noDeberiaObtenerSobremantelNoExistente() {
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

        assertThrows(DomainException.class, () -> service.obtenerSobremantel(UUID.randomUUID()));
    }

    @Test
    void noDeberiaActualizarSobremantelNoExistente() {
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

        var color = service.crearColor(new ColorCommand("Azul", "#0000FF"));
        assertThrows(DomainException.class, () -> 
                service.actualizarSobremantel(UUID.randomUUID(), new CatalogoConColorCommand("Nuevo", color.id()))
        );
    }

    @Test
    void noDeberiaDesactivarTipoAdicionalNoExistente() {
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

        assertThrows(DomainException.class, () -> service.desactivarTipoAdicional(UUID.randomUUID()));
    }

    @Test
    void noDeberiaObtenerTipoAdicionalNoExistente() {
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

        assertThrows(DomainException.class, () -> service.obtenerTipoAdicional(UUID.randomUUID()));
    }

    @Test
    void noDeberiaActualizarTipoAdicionalNoExistente() {
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

        assertThrows(DomainException.class, () -> 
                service.actualizarTipoAdicional(UUID.randomUUID(), new TipoAdicionalCommand("Nuevo", ModoCobroAdicional.SERVICIO, new BigDecimal("10000.00")))
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
