package com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.ActualizarItemCotizacionCommand;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.CotizacionView;

public interface ActualizarItemCotizacionUseCase {

    CotizacionView ejecutar(ActualizarItemCotizacionCommand command);
}
