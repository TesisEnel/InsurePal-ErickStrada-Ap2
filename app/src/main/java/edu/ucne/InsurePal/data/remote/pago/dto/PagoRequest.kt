package edu.ucne.InsurePal.data.remote.pago.dto

data class PagoRequest(
    val polizaId: String,
    val usuarioId: Int,
    val monto: Double,
    val numeroTarjeta: String,
    val fechaVencimiento: String,
    val cvv: String,
    val titular: String
)