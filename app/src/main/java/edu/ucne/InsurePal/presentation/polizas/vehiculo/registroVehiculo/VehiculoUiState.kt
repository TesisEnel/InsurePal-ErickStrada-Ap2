package edu.ucne.InsurePal.presentation.polizas.vehiculo.registroVehiculo

data class VehiculoUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,


    val usuarioId: Int? = null,

    val vehiculoIdCreado: String? = null,


    val name: String = "",
    val marca: String = "",
    val modelo: String = "",
    val anio: String = "",
    val color: String = "",
    val placa: String = "",
    val chasis: String = "",
    val valorMercado: String = "",
    val coverageType: String = "Full Cobertura",

    val marcasDisponibles: List<String> = emptyList(),
    val modelosDisponibles: List<String> = emptyList()
)