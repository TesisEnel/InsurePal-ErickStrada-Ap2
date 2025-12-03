package edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto

data class SeguroVehiculoRequest(
    val name: String,
    val usuarioId: Int,
    val marca: String,
    val modelo: String,
    val anio: String,
    val color: String,
    val placa: String,
    val chasis: String,
    val valorMercado: Double,
    val coverageType: String,
    val imagenVehiculo: String? = null,
    val status: String = "Cotizando",
    val expirationDate: String? = "",
    val esPagado: Boolean = false,
    val fechaPago: String? = null
)