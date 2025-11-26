package edu.ucne.InsurePal.presentation.polizas.vehiculo.cotizacionVehiculo

sealed interface CotizacionVehiculoEvent {
    data object OnContinuarPagoClick : CotizacionVehiculoEvent
    data object OnVolverClick : CotizacionVehiculoEvent
}