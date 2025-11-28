package edu.ucne.InsurePal.data.remote.reclamoVehiculo

data class ReclamoResponse(
    val id: String,

    val folio: String?,

    val polizaId: String,

    val usuarioId: Int,

    val descripcion: String,

    val direccion: String,

    val tipoIncidente: String,

    val fechaIncidente: String,

    val imagenUrl: String,

    val status: String,

    val motivoRechazo: String?,

    val fechaCreacion: String,

    val fechaActualizacion: String?
)