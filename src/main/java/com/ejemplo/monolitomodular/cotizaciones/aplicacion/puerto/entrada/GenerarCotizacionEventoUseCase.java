package com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.CotizacionView;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.GenerarCotizacionEventoCommand;

public interface GenerarCotizacionEventoUseCase {

    CotizacionView ejecutar(GenerarCotizacionEventoCommand command);
}
