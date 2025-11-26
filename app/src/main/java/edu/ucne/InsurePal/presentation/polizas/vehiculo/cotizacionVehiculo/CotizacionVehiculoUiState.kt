package edu.ucne.InsurePal.presentation.polizas.vehiculo.cotizacionVehiculo

data class CotizacionVehiculoUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val vehiculoDescripcion: String = "",
    val valorMercado: Double = 0.0,
    val cobertura: String = "",

    val primaNeta: Double = 0.0,
    val impuestos: Double = 0.0,
    val totalPagar: Double = 0.0
)