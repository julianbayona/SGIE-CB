package com.ejemplo.monolitomodular.shared.dominio.excepcion;

public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
