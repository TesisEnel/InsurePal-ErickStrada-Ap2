package edu.ucne.InsurePal.domain.polizas.vehiculo.repository

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.SeguroVehiculoRequest
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.SeguroVehiculo
import edu.ucne.InsurePal.domain.polizas.vida.model.SeguroVida
import kotlinx.coroutines.flow.Flow

interface SeguroVehiculoRepository {


    fun getVehiculos(usuarioId: Int): Flow<Resource<List<SeguroVehiculo>>>

    suspend fun getVehiculo(id: String): Resource<SeguroVehiculo>

    suspend fun postVehiculo(seguro: SeguroVehiculo): Resource<SeguroVehiculo>

    suspend fun putVehiculo(id: String, seguro: SeguroVehiculo): Resource<Unit>

    suspend fun delete(id: String) : Resource<Unit>
}