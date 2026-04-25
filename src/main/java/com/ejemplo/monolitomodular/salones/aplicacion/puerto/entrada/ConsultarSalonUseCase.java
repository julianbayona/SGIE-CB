package com.ejemplo.monolitomodular.salones.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.salones.aplicacion.dto.SalonView;

import java.util.List;
import java.util.UUID;

public interface ConsultarSalonUseCase {

    SalonView obtenerPorId(UUID id);

    List<SalonView> listar();
}
