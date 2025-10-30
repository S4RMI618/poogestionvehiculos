package com.gestionvehiculos.vehiculos.enums;

public enum EstadoCargue {
    CARGADO,    // Recién cargado desde Excel
    VALIDADO,   // Validado exitosamente
    PROCESADO,  // Trasladado a tabla transaccional
    ERROR       // Con errores de validación
}
