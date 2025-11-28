package edu.ucne.InsurePal.presentation.admin.adminListaVehiculos

import edu.ucne.InsurePal.domain.polizas.vehiculo.model.SeguroVehiculo

data class ListaVehiculoUiState (
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val vehicles: List<SeguroVehiculo> = emptyList(),
    val filteredVehicles: List<SeguroVehiculo> = emptyList(),
    val searchQuery: String = "",
    val selectedVehicle: SeguroVehiculo? = null,
    val isDetailVisible: Boolean = false,
    val showPendingOnly: Boolean = false
)