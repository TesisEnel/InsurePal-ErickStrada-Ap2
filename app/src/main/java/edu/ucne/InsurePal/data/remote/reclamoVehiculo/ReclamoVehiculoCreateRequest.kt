package edu.ucne.InsurePal.data.remote.reclamoVehiculo

data class ReclamoCreateRequest(
    val polizaId: String,

    val usuarioId: Int,

    val descripcion: String,

    val direccion: String,

    val tipoIncidente: String,

    val fechaIncidente: String
)