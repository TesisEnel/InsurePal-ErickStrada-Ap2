package edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto

data class SeguroVehiculoResponse(
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
    val imagenVehiculo: String? = null,
    val status: String = "",
    val expirationDate: String? = "",
    val esPagado: Boolean = false,
    val fechaPago: String? = null
)