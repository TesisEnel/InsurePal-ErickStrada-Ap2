package edu.ucne.InsurePal.presentation.reclamoVehiculo

import java.io.File

sealed interface ReclamoEvent {

    data class DescripcionChanged(val descripcion: String) : ReclamoEvent
    data class DireccionChanged(val direccion: String) : ReclamoEvent
    data class TipoIncidenteChanged(val tipo: String) : ReclamoEvent
    data class FechaIncidenteChanged(val fecha: String) : ReclamoEvent
    data class NumCuentaChanged(val numCuenta: String) : ReclamoEvent

    data class FotoSeleccionada(val archivo: File) : ReclamoEvent
    data class GuardarReclamo(val polizaId: String, val usuarioId: Int) : ReclamoEvent

    data object ErrorVisto : ReclamoEvent
}