package com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada;

public interface ProcesarRecordatoriosAnticipoUseCase {

    int procesar(int diasAntes, int repetirCadaHoras, int limite);
}
