package edu.ucne.InsurePal.data.remote.pago.dto

data class HistorialPagoDto(
    val id: Int,
    val polizaId: String,
    val monto: Double,
    val fecha: String,
    val estado: String,
    val tarjetaMascara: String,
    val numeroConfirmacion: String
)