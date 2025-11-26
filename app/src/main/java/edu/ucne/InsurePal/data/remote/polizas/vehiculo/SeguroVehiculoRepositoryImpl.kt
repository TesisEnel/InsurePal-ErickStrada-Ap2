package edu.ucne.InsurePal.data.remote.polizas.vehiculo

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.api.RemoteDataSource
import edu.ucne.InsurePal.data.toDomain
import edu.ucne.InsurePal.data.toRequest
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.SeguroVehiculo
import edu.ucne.InsurePal.domain.polizas.vehiculo.repository.SeguroVehiculoRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SeguroVehiculoRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
): SeguroVehiculoRepository {

    override fun getVehiculos(usuarioId: Int): Flow<Resource<List<SeguroVehiculo>>> = flow {
        emit(Resource.Loading())

        when (val result = remoteDataSource.getVehiculos(usuarioId)) {
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

    override suspend fun postVehiculo(seguro: SeguroVehiculo): Resource<SeguroVehiculo> {

        val requestDto = seguro.toRequest()
        return when (val result = remoteDataSource.save(requestDto)) {
            is Resource.Success -> {
                val response = result.data
                if (response != null) {
                    Resource.Success(response.toDomain())
                } else {
                    Resource.Error("Respuesta vacía")
                }
            }
            is Resource.Error -> {
                Resource.Error(result.message ?: "Error al guardar")
            }
            else -> {
                Resource.Error("Error desconocido al guardar")
            }
        }
    }

    override suspend fun putVehiculo(id: String, seguro: SeguroVehiculo): Resource<Unit> {

        val requestDto = seguro.toRequest()

        return when(val result = remoteDataSource.update(id, requestDto)){
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

    override suspend fun getVehiculo(id: String): Resource<SeguroVehiculo> {
        if (id.isBlank()) {
            return Resource.Error("ID inválido")
        }
        return when(val result = remoteDataSource.getVehiculo(id)){
            is Resource.Success -> {
                val vehiculo = result.data?.toDomain()
                if (vehiculo != null) {
                    Resource.Success(vehiculo)
                } else {
                    Resource.Error("Vehículo no encontrado")
                }
            }
            is Resource.Error -> {
                Resource.Error(result.message ?: "Error al obtener vehículo")
            }
            is Resource.Loading -> {
                Resource.Loading()
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