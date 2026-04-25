package com.ejemplo.monolitomodular.eventos.dominio.puerto.salida;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.HistorialEstadoEvento;

public interface HistorialEstadoEventoRepository {

    HistorialEstadoEvento guardar(HistorialEstadoEvento historialEstadoEvento);
}
