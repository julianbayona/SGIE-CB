package com.ejemplo.monolitomodular.calendario.dominio.puerto.salida;

import com.ejemplo.monolitomodular.calendario.aplicacion.dto.SincronizarGoogleCalendarCommand;
import com.ejemplo.monolitomodular.calendario.aplicacion.dto.SincronizarGoogleCalendarResult;

public interface GoogleCalendarPort {

    SincronizarGoogleCalendarResult sincronizar(SincronizarGoogleCalendarCommand command);
}
