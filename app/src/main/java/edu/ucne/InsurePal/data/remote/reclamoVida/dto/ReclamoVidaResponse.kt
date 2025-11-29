package edu.ucne.InsurePal.data.remote.reclamoVida.dto

data class ReclamoVidaCreateRequest(
    val polizaId: String,

    val usuarioId: Int,

    val nombreAsegurado: String,

    val descripcion: String,

    val lugarFallecimiento: String,

    val causaMuerte: String,

    val fechaFallecimiento: String,

    val numCuenta: String
)