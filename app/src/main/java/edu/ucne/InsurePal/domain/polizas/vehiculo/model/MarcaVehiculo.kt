package edu.ucne.InsurePal.domain.polizas.vehiculo.model

data class MarcaVehiculo(
    val nombre: String,
    val modelos: List<ModeloVehiculo>
)