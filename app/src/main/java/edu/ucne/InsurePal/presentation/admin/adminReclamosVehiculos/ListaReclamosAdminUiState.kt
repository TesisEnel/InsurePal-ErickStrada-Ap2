package edu.ucne.InsurePal.presentation.admin.adminReclamosVehiculos

import edu.ucne.InsurePal.domain.reclamoVehiculo.model.ReclamoVehiculo

data class ListaReclamosAdminUiState(
    val isLoading: Boolean = false,
    val reclamos: List<ReclamoVehiculo> = emptyList(),
    val error: String? = null
)