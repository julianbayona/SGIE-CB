package com.ejemplo.monolitomodular.catalogos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CatalogosModeloTest {

    // ==================== COLOR ====================

    @Test
    void deberiaCrearColorNuevo() {
        Color color = Color.nuevo("Rojo", "#FF0000");

        assertNotNull(color.getId());
        assertEquals("Rojo", color.getNombre());
        assertEquals("#FF0000", color.getCodigoHex());
        assertTrue(color.isActivo());
    }

    @Test
    void deberiaReconstruirColor() {
        UUID id = UUID.randomUUID();
        Color color = Color.reconstruir(id, "Azul", "#0000FF", false);

        assertEquals(id, color.getId());
        assertEquals("Azul", color.getNombre());
        assertFalse(color.isActivo());
    }

    @Test
    void deberiaActualizarColor() {
        Color color = Color.nuevo("Rojo", "#FF0000");
        Color actualizado = color.actualizar("Rojo Oscuro", "#8B0000");

        assertEquals(color.getId(), actualizado.getId());
        assertEquals("Rojo Oscuro", actualizado.getNombre());
        assertEquals("#8B0000", actualizado.getCodigoHex());
    }

    @Test
    void deberiaDesactivarColor() {
        Color color = Color.nuevo("Verde", "#00FF00");
        Color desactivado = color.desactivar();

        assertEquals(color.getId(), desactivado.getId());
        assertFalse(desactivado.isActivo());
    }

    @Test
    void noDeberiaCrearColorConNombreNulo() {
        assertThrows(DomainException.class, () -> Color.nuevo(null, "#FF0000"));
    }

    @Test
    void noDeberiaCrearColorConNombreBlank() {
        assertThrows(DomainException.class, () -> Color.nuevo("   ", "#FF0000"));
    }

    @Test
    void noDeberiaCrearColorConCodigoHexNulo() {
        assertThrows(DomainException.class, () -> Color.nuevo("Rojo", null));
    }

    @Test
    void noDeberiaCrearColorConCodigoHexInvalido() {
        assertThrows(DomainException.class, () -> Color.nuevo("Rojo", "rojo"));
    }

    @Test
    void noDeberiaCrearColorConCodigoHexSinAlmohadilla() {
        assertThrows(DomainException.class, () -> Color.nuevo("Rojo", "FF0000"));
    }

    @Test
    void noDeberiaCrearColorConCodigoHexCorto() {
        assertThrows(DomainException.class, () -> Color.nuevo("Rojo", "#F00"));
    }

    @Test
    void deberiaConvertirCodigoHexAMayusculas() {
        Color color = Color.nuevo("Rojo", "#ff0000");
        assertEquals("#FF0000", color.getCodigoHex());
    }

    // ==================== TIPO EVENTO ====================

    @Test
    void deberiaCrearTipoEventoNuevo() {
        TipoEvento tipoEvento = TipoEvento.nuevo("Boda", "Evento social formal");

        assertNotNull(tipoEvento.getId());
        assertEquals("Boda", tipoEvento.getNombre());
        assertEquals("Evento social formal", tipoEvento.getDescripcion());
        assertTrue(tipoEvento.isActivo());
    }

    @Test
    void deberiaCrearTipoEventoConDescripcionNula() {
        TipoEvento tipoEvento = TipoEvento.nuevo("Conferencia", null);
        assertEquals("", tipoEvento.getDescripcion());
    }

    @Test
    void deberiaActualizarTipoEvento() {
        TipoEvento tipoEvento = TipoEvento.nuevo("Boda", "Formal");
        TipoEvento actualizado = tipoEvento.actualizar("Matrimonio", "Ceremonia civil");

        assertEquals(tipoEvento.getId(), actualizado.getId());
        assertEquals("Matrimonio", actualizado.getNombre());
        assertEquals("Ceremonia civil", actualizado.getDescripcion());
    }

    @Test
    void deberiaDesactivarTipoEvento() {
        TipoEvento tipoEvento = TipoEvento.nuevo("Grado", "Ceremonia");
        TipoEvento desactivado = tipoEvento.desactivar();

        assertEquals(tipoEvento.getId(), desactivado.getId());
        assertFalse(desactivado.isActivo());
    }

    @Test
    void deberiaReconstruirTipoEvento() {
        UUID id = UUID.randomUUID();
        TipoEvento tipoEvento = TipoEvento.reconstruir(id, "Quinceañera", "Celebración", false);

        assertEquals(id, tipoEvento.getId());
        assertFalse(tipoEvento.isActivo());
    }

    @Test
    void noDeberiaCrearTipoEventoConNombreNulo() {
        assertThrows(DomainException.class, () -> TipoEvento.nuevo(null, "Descripcion"));
    }

    @Test
    void noDeberiaCrearTipoEventoConNombreBlank() {
        assertThrows(DomainException.class, () -> TipoEvento.nuevo("   ", "Descripcion"));
    }

    // ==================== TIPO ADICIONAL ====================

    @Test
    void deberiaCrearTipoAdicionalNuevo() {
        TipoAdicional adicional = TipoAdicional.nuevo("Video beam", ModoCobroAdicional.SERVICIO, new BigDecimal("120000.00"));

        assertNotNull(adicional.getId());
        assertEquals("Video beam", adicional.getNombre());
        assertEquals(ModoCobroAdicional.SERVICIO, adicional.getModoCobro());
        assertEquals(new BigDecimal("120000.00"), adicional.getPrecioBase());
        assertTrue(adicional.isActivo());
    }

    @Test
    void deberiaCrearTipoAdicionalConPrecioCero() {
        TipoAdicional adicional = TipoAdicional.nuevo("Servicio gratis", ModoCobroAdicional.SERVICIO, BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, adicional.getPrecioBase());
    }

    @Test
    void deberiaActualizarTipoAdicional() {
        TipoAdicional adicional = TipoAdicional.nuevo("Musica", ModoCobroAdicional.SERVICIO, new BigDecimal("50000"));
        TipoAdicional actualizado = adicional.actualizar("Musica Premium", ModoCobroAdicional.UNIDAD, new BigDecimal("75000"));

        assertEquals(adicional.getId(), actualizado.getId());
        assertEquals("Musica Premium", actualizado.getNombre());
        assertEquals(ModoCobroAdicional.UNIDAD, actualizado.getModoCobro());
    }

    @Test
    void deberiaDesactivarTipoAdicional() {
        TipoAdicional adicional = TipoAdicional.nuevo("Fotografia", ModoCobroAdicional.SERVICIO, new BigDecimal("100000"));
        TipoAdicional desactivado = adicional.desactivar();

        assertFalse(desactivado.isActivo());
        assertEquals(adicional.getId(), desactivado.getId());
    }

    @Test
    void deberiaReconstruirTipoAdicional() {
        UUID id = UUID.randomUUID();
        TipoAdicional adicional = TipoAdicional.reconstruir(id, "Decoracion", ModoCobroAdicional.SERVICIO,
                new BigDecimal("30000"), false);

        assertEquals(id, adicional.getId());
        assertFalse(adicional.isActivo());
    }

    @Test
    void noDeberiaCrearTipoAdicionalConNombreNulo() {
        assertThrows(DomainException.class, () ->
                TipoAdicional.nuevo(null, ModoCobroAdicional.SERVICIO, new BigDecimal("100"))
        );
    }

    @Test
    void noDeberiaCrearTipoAdicionalConNombreBlank() {
        assertThrows(DomainException.class, () ->
                TipoAdicional.nuevo("   ", ModoCobroAdicional.SERVICIO, new BigDecimal("100"))
        );
    }

    @Test
    void noDeberiaCrearTipoAdicionalConModuCobroNulo() {
        assertThrows(DomainException.class, () ->
                TipoAdicional.nuevo("Servicio", null, new BigDecimal("100"))
        );
    }

    @Test
    void noDeberiaCrearTipoAdicionalConPrecioNulo() {
        assertThrows(DomainException.class, () ->
                TipoAdicional.nuevo("Servicio", ModoCobroAdicional.SERVICIO, null)
        );
    }

    @Test
    void noDeberiaCrearTipoAdicionalConPrecioNegativo() {
        assertThrows(DomainException.class, () ->
                TipoAdicional.nuevo("Servicio", ModoCobroAdicional.SERVICIO, new BigDecimal("-1"))
        );
    }

    // ==================== MANTEL ====================

    @Test
    void deberiaCrearMantelNuevo() {
        UUID colorId = UUID.randomUUID();
        Mantel mantel = Mantel.nuevo("Mantel Rojo", colorId);

        assertNotNull(mantel.getId());
        assertEquals("Mantel Rojo", mantel.getNombre());
        assertEquals(colorId, mantel.getColorId());
        assertTrue(mantel.isActivo());
    }

    @Test
    void deberiaActualizarMantel() {
        UUID colorId1 = UUID.randomUUID();
        UUID colorId2 = UUID.randomUUID();
        Mantel mantel = Mantel.nuevo("Mantel Azul", colorId1);
        Mantel actualizado = mantel.actualizar("Mantel Verde", colorId2);

        assertEquals(mantel.getId(), actualizado.getId());
        assertEquals("Mantel Verde", actualizado.getNombre());
        assertEquals(colorId2, actualizado.getColorId());
    }

    @Test
    void deberiaDesactivarMantel() {
        Mantel mantel = Mantel.nuevo("Mantel Negro", UUID.randomUUID());
        Mantel desactivado = mantel.desactivar();

        assertFalse(desactivado.isActivo());
        assertEquals(mantel.getId(), desactivado.getId());
    }

    @Test
    void deberiaReconstruirMantel() {
        UUID id = UUID.randomUUID();
        UUID colorId = UUID.randomUUID();
        Mantel mantel = Mantel.reconstruir(id, "Mantel Blanco", colorId, false);

        assertEquals(id, mantel.getId());
        assertEquals(colorId, mantel.getColorId());
        assertFalse(mantel.isActivo());
    }

    @Test
    void noDeberiaCrearMantelConNombreNulo() {
        assertThrows(DomainException.class, () -> Mantel.nuevo(null, UUID.randomUUID()));
    }

    @Test
    void noDeberiaCrearMantelConNombreBlank() {
        assertThrows(DomainException.class, () -> Mantel.nuevo("   ", UUID.randomUUID()));
    }

    @Test
    void noDeberiaCrearMantelConColorIdNulo() {
        assertThrows(NullPointerException.class, () -> Mantel.nuevo("Mantel Test", null));
    }
}