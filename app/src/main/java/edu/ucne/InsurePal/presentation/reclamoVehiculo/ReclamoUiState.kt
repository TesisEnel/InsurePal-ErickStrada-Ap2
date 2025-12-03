package edu.ucne.InsurePal.presentation.reclamoVehiculo

import java.io.File

data class ReclamoUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val esExitoso: Boolean = false,

    val descripcion: String = "",
    val errorDescripcion: String? = null,

    val direccion: String = "",
    val errorDireccion: String? = null,

    val tipoIncidente: String = "",
    val errorTipoIncidente: String? = null,

    val fechaIncidente: String = "",
    val errorFechaIncidente: String? = null,

    val numCuenta: String = "",
    val errorNumCuenta: String? = null,

    val fotoEvidencia: File? = null,
    val errorFotoEvidencia: String? = null,

    val camposValidos: Boolean = false
)