package edu.ucne.InsurePal.data.remote.polizas.vehiculo

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.api.RemoteDataSource
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.SeguroVehiculoRequest
import edu.ucne.InsurePal.data.toDomain
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.SeguroVehiculo
import edu.ucne.InsurePal.domain.polizas.vehiculo.repository.SeguroVehiculoRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SeguroVehiculoRepositoryImpl @Inject constructor(
    val remoteDataSource: RemoteDataSource
): SeguroVehiculoRepository {

    override suspend fun getVehiculos(): Flow<Resource<List<SeguroVehiculo>>> = flow {
        emit(Resource.Loading())
        when (val result = remoteDataSource.getVehiculos()) {
            is Resource.Success -> {
                val list = result.data?.map { it.toDomain() } ?: emptyList()
                emit(Resource.Success(list))
            }
            is Resource.Error -> {
                emit(Resource.Error(result.message ?: "Error al obtener vehiculos"))
            }
            is Resource.Loading -> {
                emit(Resource.Loading())
            }
        }
    }

    override suspend fun postVehiculo(req: SeguroVehiculoRequest): Resource<SeguroVehiculo> {

        return when (val result = remoteDataSource.save(req)) {
            is Resource.Success -> {
                val response = result.data
                Resource.Success(response?.toDomain())
            }
            is Resource.Error -> {
                Resource.Error(result.message ?: "Error al guardar")
            }
            else -> {
                Resource.Error("Error desconocido al guardar")
            }
        }
    }

    override suspend fun putVehiculo(id: String, req: SeguroVehiculoRequest): Resource<Unit> {
        return when(val result = remoteDataSource.update(id, req)){
            is Resource.Success -> {
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Resource.Error(result.message ?: "Error al actualizar")
            }
            else -> {
                Resource.Error("Error desconocido al actualizar")
            }
        }
    }

    override suspend fun getVehiculo(id: String?): Flow<Resource<SeguroVehiculo>> = flow {
        if (id == null) {
            emit(Resource.Error("ID de vehiculo no puede ser nulo"))
            return@flow
        }

        emit(Resource.Loading())
        when(val result = remoteDataSource.getVehiculo(id)){
            is Resource.Success -> {
                val vehiculo = result.data?.toDomain()
                if (vehiculo != null) {
                    emit(Resource.Success(vehiculo))
                } else {
                    emit(Resource.Error("Vehiculo no encontrado o datos corruptos"))
                }
            }
            is Resource.Error -> {
                emit(Resource.Error(result.message ?: "Error al obtener vehiculo"))
            }
            is Resource.Loading -> {
                emit(Resource.Loading())
            }
        }
    }
    override suspend fun delete(id: String): Resource<Unit> {
        return when (val result = remoteDataSource.deleteVehiculo(id)) {
            is Resource.Success -> {
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Resource.Error(result.message ?: "Error al eliminar el vehículo")
            }
            else -> {
                Resource.Error("Error desconocido al eliminar")
            }
        }
    }


}