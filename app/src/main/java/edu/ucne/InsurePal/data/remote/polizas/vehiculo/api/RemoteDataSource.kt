package edu.ucne.InsurePal.data.remote.polizas.vehiculo.api

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.SeguroVehiculoRequest
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.SeguroVehiculoResponse
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val api: SeguroVehiculoApiService
) {
    suspend fun save(request: SeguroVehiculoRequest): Resource<SeguroVehiculoResponse> {
        return try {
            val response = api.postVehiculo(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body)
                } else {
                    Resource.Error("Respuesta vacía del servidor")
                }
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun update(id: String, request: SeguroVehiculoRequest): Resource<Unit> {
        return try {
            val response = api.putVehiculo(id, request)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getVehiculo(id: String): Resource<SeguroVehiculoResponse>{
        return try{
            val response = api.getVehiculo(id)
            if(response.isSuccessful){
                response.body().let { Resource.Success(it) }
            }else {
                return Resource.Error("Error ${response.code()}: ${response.message()}")
            }
        }catch (e: Exception){
            return Resource.Error("Error: ${e.localizedMessage ?: "Ocurrió un error"}")
        }
    }

    suspend fun getVehiculos(usuarioId: Int): Resource<List<SeguroVehiculoResponse>> {
        return try {
            val response = api.getVehiculos()
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía al obtener los vehiculos")
            } else {
                Resource.Error("HTTP ${response.code()} al obtener lista de vehiculos: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red al obtener vehiculos")
        }
    }

    suspend fun deleteVehiculo(id: String): Resource<Unit> {
        return try {
            val response = api.deleteVehiculo(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }
}