package edu.ucne.InsurePal.presentation.detallePoliza

sealed interface DetallePolizaEvent {
    data object OnEliminarPoliza : DetallePolizaEvent
    data class OnCambiarPlanVehiculo(val nuevoPlan: String) : DetallePolizaEvent
    data object OnErrorDismiss : DetallePolizaEvent
}