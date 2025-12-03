package edu.ucne.InsurePal.presentation.reclamoVida

import java.io.File

sealed interface ReclamoVidaEvent {
    data class NombreAseguradoChanged(val nombre: String) : ReclamoVidaEvent
    data class DescripcionChanged(val descripcion: String) : ReclamoVidaEvent
    data class LugarFallecimientoChanged(val lugar: String) : ReclamoVidaEvent
    data class CausaMuerteChanged(val causa: String) : ReclamoVidaEvent
    data class FechaFallecimientoChanged(val fecha: String) : ReclamoVidaEvent
    data class NumCuentaChanged(val cuenta: String) : ReclamoVidaEvent

    data class ActaDefuncionSeleccionada(val archivo: File) : ReclamoVidaEvent

    data class IdentificacionSeleccionada(val archivo: File) : ReclamoVidaEvent
    data class GuardarReclamo(val polizaId: String, val usuarioId: Int) : ReclamoVidaEvent
    data object ErrorVisto : ReclamoVidaEvent
}