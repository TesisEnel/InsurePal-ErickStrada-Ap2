package edu.ucne.InsurePal.data.remote.pago.dto

data class PagoResponse(
    val exito: Boolean,
    val mensaje: String,
    val numeroTransaccion: String?,
    val fechaProcesado: String
)