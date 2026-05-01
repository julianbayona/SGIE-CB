package com.ejemplo.monolitomodular.montajes.dominio.puerto.salida;

import com.ejemplo.monolitomodular.montajes.dominio.modelo.Montaje;

import java.util.Optional;
import java.util.UUID;

public interface MontajeRepository {

    Montaje guardar(Montaje montaje);

    Optional<Montaje> buscarPorReservaId(UUID reservaId);
}
