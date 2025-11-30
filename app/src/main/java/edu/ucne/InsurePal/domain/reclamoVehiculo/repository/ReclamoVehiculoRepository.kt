package edu.ucne.InsurePal.domain.reclamoVehiculo.repository

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.reclamoVehiculo.model.CrearReclamoVehiculoParams
import edu.ucne.InsurePal.domain.reclamoVehiculo.model.ReclamoVehiculo

interface ReclamoVehiculoRepository {
    suspend fun crearReclamoVehiculo(params: CrearReclamoVehiculoParams): Resource<ReclamoVehiculo>

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