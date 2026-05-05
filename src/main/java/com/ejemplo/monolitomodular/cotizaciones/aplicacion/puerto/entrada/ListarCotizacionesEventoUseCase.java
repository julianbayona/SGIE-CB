package com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.CotizacionView;

import java.util.List;
import java.util.UUID;

public interface ListarCotizacionesEventoUseCase {

    List<CotizacionView> listarPorEvento(UUID eventoId);
}
