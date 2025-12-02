package edu.ucne.InsurePal.presentation.polizas.vida.reclamoVida

import java.io.File

data class ReclamoVidaUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val esExitoso: Boolean = false,

    val nombreAsegurado: String = "",
    val errorNombreAsegurado: String? = null,

    val descripcion: String = "",
    val errorDescripcion: String? = null,

    val lugarFallecimiento: String = "",
    val errorLugarFallecimiento: String? = null,

    val causaMuerte: String = "",
    val errorCausaMuerte: String? = null,

    val fechaFallecimiento: String = "",
    val errorFechaFallecimiento: String? = null,

    val numCuenta: String = "",
    val errorNumCuenta: String? = null,

    val archivoActa: File? = null,
    val errorArchivoActa: String? = null,

    val camposValidos: Boolean = false
)