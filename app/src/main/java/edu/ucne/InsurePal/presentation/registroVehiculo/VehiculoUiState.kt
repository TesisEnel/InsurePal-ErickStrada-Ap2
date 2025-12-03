package edu.ucne.InsurePal.presentation.registroVehiculo

data class VehiculoUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,

    val usuarioId: Int? = null,
    val vehiculoIdCreado: String? = null,

    val name: String = "",
    val errorName: String? = null,

    val marca: String = "",
    val errorMarca: String? = null,
    val modelo: String = "",
    val errorModelo: String? = null,

    val anio: String = "",
    val errorAnio: String? = null,

    val color: String = "",
    val errorColor: String? = null,

    val placa: String = "",
    val errorPlaca: String? = null,
    val chasis: String = "",
    val errorChasis: String? = null,

    val valorMercado: String = "",
    val errorValorMercado: String? = null,

    val coverageType: String = "Full Cobertura",

    val marcasDisponibles: List<String> = emptyList(),
    val modelosDisponibles: List<String> = emptyList()
)