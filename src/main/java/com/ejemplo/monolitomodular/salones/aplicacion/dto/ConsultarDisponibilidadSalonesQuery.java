package com.ejemplo.monolitomodular.salones.aplicacion.dto;

import java.time.LocalDateTime;

public record ConsultarDisponibilidadSalonesQuery(
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin,
        Integer capacidadMinima
) {
}
