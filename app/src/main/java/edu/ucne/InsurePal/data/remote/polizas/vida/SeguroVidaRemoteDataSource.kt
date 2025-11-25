package edu.ucne.InsurePal.data.remote.polizas.vida


import edu.ucne.InsurePal.data.Resource
import javax.inject.Inject

class SeguroVidaRemoteDataSource @Inject constructor(
    private val api: SeguroVidaApiService
) {
    private val errorNetwork = "Error de conexión con el servidor"

    suspend fun getSegurosVida(usuarioId: Int): Resource<List<SeguroVidaResponse>> {
        return try {
            val response = api.getSegurosVida(usuarioId)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Success(emptyList())
            } else {
                Resource.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: errorNetwork)
        }
    }

    suspend fun getSeguroVidaById(id: String): Resource<SeguroVidaResponse> {
        return try {
            val response = api.getSeguroVidaById(id)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("No se encontró el seguro")
            } else {
                Resource.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: errorNetwork)
        }
    }

    suspend fun saveSeguroVida(request: SeguroVidaRequest): Resource<SeguroVidaResponse> {
        return try {
            val response = api.saveSeguroVida(request)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: errorNetwork)
        }
    }

    suspend fun updateSeguroVida(id: String, request: SeguroVidaRequest): Resource<SeguroVidaResponse> {
        return try {
            val response = api.updateSeguroVida(id, request)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Error al actualizar la póliza")
            } else {
                Resource.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: errorNetwork)
        }
    }

    suspend fun deleteSeguroVida(id: String): Resource<Unit> {
        return try {
            val response = api.deleteSeguroVida(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: errorNetwork)
        }
    }
}