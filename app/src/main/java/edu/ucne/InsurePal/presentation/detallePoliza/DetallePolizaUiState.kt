package edu.ucne.InsurePal.presentation.detallePoliza

data class DetallePolizaUiState (
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDeleted: Boolean = false,

    val policyId: String = "",
    val title: String = "",
    val subtitle: String = "",
    val status: String = "",
    val price: Double = 0.0,
    val isPaid: Boolean = false,
    val coverageType: String = "",

    val details: Map<String, String> = emptyMap()
)