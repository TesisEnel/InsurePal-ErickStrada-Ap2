package edu.ucne.InsurePal.domain.reclamoVida.model

import java.io.File

data class CrearReclamoVidaParams(
    val polizaId: String,
    val usuarioId: Int,
    val nombreAsegurado: String,
    val descripcion: String,
    val lugarFallecimiento: String,
    val causaMuerte: String,
    val fechaFallecimiento: String,
    val numCuenta: String,
    val actaDefuncion: File
)