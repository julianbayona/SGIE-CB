package com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.CotizacionView;

import java.util.UUID;

public interface EnviarCotizacionEmailUseCase {

    CotizacionView enviarPorEmail(UUID cotizacionId);
}
