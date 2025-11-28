package edu.ucne.InsurePal.presentation.polizas.vehiculo.reclamoVehiculo

import java.io.File

data class ReclamoUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val esExitoso: Boolean = false,

    val descripcion: String = "",
    val direccion: String = "",
    val tipoIncidente: String = "",
    val fechaIncidente: String = "",

    val fotoEvidencia: File? = null,


    val camposValidos: Boolean = false
)