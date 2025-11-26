package edu.ucne.InsurePal.domain.pago.model

data class TarjetaCredito(
    val titular: String,
    val numero: String,
    val fechaVencimiento: String,
    val cvv: String
)