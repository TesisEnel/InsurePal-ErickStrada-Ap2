package edu.ucne.InsurePal.data.remote.polizas.vehiculo.api

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.MarcaVehiculoDto
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.SeguroVehiculoRequest
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.SeguroVehiculoResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val api: SeguroVehiculoApiService
) {
    private val errorNetwork = "Error de conexión con el servidor"
    suspend fun save(request: SeguroVehiculoRequest): Resource<SeguroVehiculoResponse> {
        return try {
            val imagePart: MultipartBody.Part? = request.imagenVehiculo?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("imagenVehiculo", file.name, reqFile)
                } else {
                    null
                }
            }

            val textType = "text/plain".toMediaTypeOrNull()

            val dataMap = mutableMapOf<String, RequestBody>().apply {
                put("name", request.name.toRequestBody(textType))
                put("usuarioId", request.usuarioId.toString().toRequestBody(textType))
                put("marca", request.marca.toRequestBody(textType))
                put("modelo", request.modelo.toRequestBody(textType))
                put("anio", request.anio.toRequestBody(textType))
                put("color", request.color.toRequestBody(textType))
                put("placa", request.placa.toRequestBody(textType))
                put("chasis", request.chasis.toRequestBody(textType))
                put("valorMercado", request.valorMercado.toString().toRequestBody(textType))
                put("coverageType", request.coverageType.toRequestBody(textType))
                put("status", request.status.toRequestBody(textType))

                request.expirationDate?.let { put("expirationDate", it.toRequestBody(textType)) }
                request.fechaPago?.let { put("fechaPago", it.toRequestBody(textType)) }
                put("esPagado", request.esPagado.toString().toRequestBody(textType))
            }

            val response = api.postVehiculo(imagePart, dataMap)

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
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: errorNetwork)
        }
    }

    suspend fun getAllVehiculos(): Resource<List<SeguroVehiculoResponse>> {
        return try {
            val response = api.getAllVehiculos()
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("La lista de vehículos está vacía")
            } else {
                Resource.Error("HTTP ${response.code()} al obtener todos los vehículos: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red al obtener todos los vehículos")
        }
    }

    suspend fun update(id: String?, request: SeguroVehiculoRequest): Resource<Unit> {
        return try {
            val response = api.putVehiculo(id, request)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: errorNetwork)
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
            val response = api.getVehiculos(usuarioId)
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

    suspend fun getMarcas(): Resource<List<MarcaVehiculoDto>> {
        return try {
            val response = api.getMarcas()
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Success(emptyList())
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: errorNetwork)
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
            Resource.Error(e.localizedMessage ?: errorNetwork)
        }
    }
}