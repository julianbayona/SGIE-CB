package com.ejemplo.monolitomodular.montajes.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.montajes.aplicacion.dto.MontajeView;

import java.util.UUID;

public interface ConsultarMontajeUseCase {

    MontajeView obtenerPorReservaRaizId(UUID reservaRaizId);
}
