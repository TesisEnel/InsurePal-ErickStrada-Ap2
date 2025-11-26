package edu.ucne.InsurePal.presentation.polizas.vehiculo.registroVehiculo

sealed interface VehiculoEvent {

    data class OnNameChanged(val name: String) : VehiculoEvent
    data class OnMarcaChanged(val marca: String) : VehiculoEvent
    data class OnModeloChanged(val modelo: String) : VehiculoEvent
    data class OnAnioChanged(val anio: String) : VehiculoEvent
    data class OnColorChanged(val color: String) : VehiculoEvent
    data class OnPlacaChanged(val placa: String) : VehiculoEvent
    data class OnChasisChanged(val chasis: String) : VehiculoEvent
    data class OnValorChanged(val valor: String) : VehiculoEvent
    data class OnCoverageChanged(val type: String) : VehiculoEvent


    data object OnGuardarClick : VehiculoEvent
    data object OnMessageShown : VehiculoEvent

    data object OnExitoNavegacion : VehiculoEvent

}