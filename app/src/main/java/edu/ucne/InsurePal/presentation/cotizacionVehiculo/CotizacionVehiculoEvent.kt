package edu.ucne.InsurePal.presentation.cotizacionVehiculo

sealed interface CotizacionVehiculoEvent {
    data object OnContinuarPagoClick : CotizacionVehiculoEvent
    data object OnVolverClick : CotizacionVehiculoEvent
}