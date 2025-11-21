package edu.ucne.InsurePal.domain.polizas.vehiculo.model

import java.time.LocalDate

data class SeguroVehiculo (
    val idPoliza: String? = null,
    val name: String,
    val status: String,
    val expirationDate: LocalDate,

    val placa: String,
    val modeloVehiculo: String,
    val coverageType: String
)

