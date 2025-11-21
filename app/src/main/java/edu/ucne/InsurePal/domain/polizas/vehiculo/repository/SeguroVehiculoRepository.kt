package edu.ucne.InsurePal.domain.polizas.vehiculo.repository

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.SeguroVehiculoRequest
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.SeguroVehiculo
import kotlinx.coroutines.flow.Flow

interface SeguroVehiculoRepository {
    suspend fun getVehiculos(): Flow<Resource<List<SeguroVehiculo>>>
    suspend fun getVehiculo(id: String): Flow<Resource<SeguroVehiculo>>
    suspend fun postVehiculo(req: SeguroVehiculoRequest): Resource<SeguroVehiculo>
    suspend fun putVehiculo(id: String, req: SeguroVehiculoRequest): Resource<Unit>
}