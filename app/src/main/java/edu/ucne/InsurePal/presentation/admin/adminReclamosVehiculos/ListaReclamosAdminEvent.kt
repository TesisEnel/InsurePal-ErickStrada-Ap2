package edu.ucne.InsurePal.presentation.admin.adminReclamosVehiculos

sealed interface ListaReclamosAdminEvent {
    data object OnCargar : ListaReclamosAdminEvent
    data class OnReclamoClick(val id: String) : ListaReclamosAdminEvent
    data object OnErrorDismiss : ListaReclamosAdminEvent
}