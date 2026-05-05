package com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.pagos.aplicacion.dto.ProgramarRecordatorioAnticipoCommand;
import com.ejemplo.monolitomodular.pagos.aplicacion.dto.RecordatorioAnticipoView;

public interface ProgramarRecordatorioAnticipoUseCase {

    RecordatorioAnticipoView ejecutar(ProgramarRecordatorioAnticipoCommand command);
}
