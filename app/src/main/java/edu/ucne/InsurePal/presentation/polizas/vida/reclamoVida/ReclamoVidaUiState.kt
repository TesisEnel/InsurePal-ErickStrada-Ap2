package edu.ucne.InsurePal.presentation.polizas.vida.reclamoVida

import java.io.File

data class ReclamoVidaUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val esExitoso: Boolean = false,

    val nombreAsegurado: String = "",
    val descripcion: String = "",
    val lugarFallecimiento: String = "",
    val causaMuerte: String = "",
    val fechaFallecimiento: String = "",
    val numCuenta: String = "",

    val archivoActa: File? = null,

    val camposValidos: Boolean = false
)