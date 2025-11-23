package edu.ucne.InsurePal.domain.polizas.vehiculo.model

data class DesglosePrima(
    val primaNeta: Double,
    val impuestos: Double,
    val total: Double
)