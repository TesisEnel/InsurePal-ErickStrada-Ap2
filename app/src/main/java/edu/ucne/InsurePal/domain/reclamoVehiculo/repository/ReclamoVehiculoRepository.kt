package edu.ucne.InsurePal.domain.reclamoVehiculo.repository

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.reclamoVehiculo.model.ReclamoVehiculo
import java.io.File

interface ReclamoVehiculoRepository {
    suspend fun crearReclamoVehiculo(
        polizaId: String,
        usuarioId: Int,
        descripcion: String,
        direccion: String,
        tipoIncidente: String,
        fechaIncidente: String,
        numCuenta : String,
        imagen: File
    ): Resource<ReclamoVehiculo>

    suspend fun cambiarEstadoReclamoVehiculo(
        reclamoId: String,
        nuevoEstado: String,
        motivoRechazo: String?
    ): Resource<ReclamoVehiculo>


    suspend fun obtenerReclamoVehiculos(
        usuarioId: Int? = null
    ): Resource<List<ReclamoVehiculo>>

    suspend fun obtenerReclamoVehiculoPorId(
        id: String
    ): Resource<ReclamoVehiculo>
}