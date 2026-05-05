package com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.DocumentoCotizacionView;

import java.util.UUID;

public interface DescargarDocumentoCotizacionUseCase {

    DocumentoCotizacionView descargar(UUID cotizacionId);
}
