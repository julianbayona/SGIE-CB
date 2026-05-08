package com.ejemplo.monolitomodular.reportes.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.reportes.aplicacion.dto.DemandaSalonView;

import java.time.LocalDate;
import java.util.List;

public interface ConsultarDemandaSalonesUseCase {

    List<DemandaSalonView> consultarDemandaSalones(LocalDate desde, LocalDate hasta);
}
