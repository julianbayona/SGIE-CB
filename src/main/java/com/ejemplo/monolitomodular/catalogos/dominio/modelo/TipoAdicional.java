package com.ejemplo.monolitomodular.catalogos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class TipoAdicional {

    private final UUID id;
    private final String nombre;
    private final ModoCobroAdicional modoCobro;
    private final BigDecimal precioBase;
    private final boolean activo;

    private TipoAdicional(UUID id, String nombre, ModoCobroAdicional modoCobro, BigDecimal precioBase, boolean activo) {
        this.id = Objects.requireNonNull(id, "El id del tipo adicional es obligatorio");
        this.nombre = validarNombre(nombre);
        this.modoCobro = validarModoCobro(modoCobro);
        this.precioBase = validarPrecioBase(precioBase);
        this.activo = activo;
    }

    public static TipoAdicional nuevo(String nombre, ModoCobroAdicional modoCobro, BigDecimal precioBase) {
        return new TipoAdicional(UUID.randomUUID(), nombre, modoCobro, precioBase, true);
    }

    public static TipoAdicional reconstruir(UUID id, String nombre, ModoCobroAdicional modoCobro, BigDecimal precioBase, boolean activo) {
        return new TipoAdicional(id, nombre, modoCobro, precioBase, activo);
    }

    public TipoAdicional actualizar(String nombre, ModoCobroAdicional modoCobro, BigDecimal precioBase) {
        return new TipoAdicional(id, nombre, modoCobro, precioBase, activo);
    }

    public TipoAdicional desactivar() {
        return new TipoAdicional(id, nombre, modoCobro, precioBase, false);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public ModoCobroAdicional getModoCobro() {
        return modoCobro;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public boolean isActivo() {
        return activo;
    }

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DomainException("El nombre del tipo adicional es obligatorio");
        }
        return nombre.trim();
    }

    private static ModoCobroAdicional validarModoCobro(ModoCobroAdicional modoCobro) {
        if (modoCobro == null) {
            throw new DomainException("El modo de cobro del tipo adicional es obligatorio");
        }
        return modoCobro;
    }

    private static BigDecimal validarPrecioBase(BigDecimal precioBase) {
        if (precioBase == null || precioBase.signum() < 0) {
            throw new DomainException("El precio base del tipo adicional no puede ser negativo");
        }
        return precioBase;
    }
}
