package com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.pagos.aplicacion.dto.AnticipoView;

import java.util.List;
import java.util.UUID;

public interface ConsultarAnticiposUseCase {

    List<AnticipoView> listarPorCotizacion(UUID cotizacionId);
}
