package edu.ucne.InsurePal.domain.pago.model

import java.time.LocalDateTime

data class Pago(
    val id: Int = 0,
    val polizaId: String,
    val usuarioId: Int,
    val fecha: LocalDateTime,
    val monto: Double,
    val tarjetaUltimosDigitos: String,
    val estado: EstadoPago,
    val numeroConfirmacion: String
)



