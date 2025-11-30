package edu.ucne.InsurePal.data.remote.reclamoVida.dto
data class ReclamoVidaResponse(

    val id: String,

    val folio: String?,

    val polizaId: String,

    val usuarioId: Int,

    val nombreAsegurado: String,

    val descripcion: String,

    val lugarFallecimiento: String,

    val causaMuerte: String,

    val fechaFallecimiento: String,

    val numCuenta: String?,

    val actaDefuncionUrl: String?,

    val identificacionUrl: String?,

    val status: String,

    val motivoRechazo: String?,

    val fechaCreacion: String
)