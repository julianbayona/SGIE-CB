package com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.pagos.aplicacion.dto.EstadoFinancieroEventoView;

import java.util.UUID;

public interface ConsultarEstadoFinancieroEventoUseCase {

    EstadoFinancieroEventoView consultar(UUID eventoId);
}
