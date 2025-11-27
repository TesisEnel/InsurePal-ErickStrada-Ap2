package edu.ucne.InsurePal.presentation.admin.adminListaVehiculos

import edu.ucne.InsurePal.domain.polizas.vehiculo.model.SeguroVehiculo

sealed interface ListaVehiculoEvent {
    data class OnSearchQueryChange(val query: String) : ListaVehiculoEvent
    data class OnSelectVehicle(val vehicle: SeguroVehiculo) : ListaVehiculoEvent
    data object OnDismissDetail : ListaVehiculoEvent
    data object Refresh : ListaVehiculoEvent
}