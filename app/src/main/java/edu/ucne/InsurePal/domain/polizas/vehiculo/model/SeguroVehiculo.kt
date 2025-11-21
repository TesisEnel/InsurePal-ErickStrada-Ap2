package edu.ucne.InsurePal.domain.polizas.vehiculo.model

import java.time.LocalDate

data class SeguroVehiculo(
    val idPoliza: String? = null,
    val usuarioId: Int,
    val name: String,
    val marca: String,
    val modelo: String,
    val anio: String,
    val color: String,
    val placa: String,
    val chasis: String,
    val valorMercado: Double,
    val coverageType: String,
    val status: String = "Cotizando",
    val expirationDate: LocalDate? = null
)

