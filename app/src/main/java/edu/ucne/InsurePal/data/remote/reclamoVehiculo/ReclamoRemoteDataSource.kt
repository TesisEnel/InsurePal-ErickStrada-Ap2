package edu.ucne.InsurePal.data.remote.reclamoVehiculo

import edu.ucne.InsurePal.data.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class ReclamoRemoteDataSource @Inject constructor(
    private val api: ReclamosApiService
) {
    private val errorNetwork = "Error de conexión con el servidor"

    suspend fun crearReclamo(request: ReclamoCreateRequest, archivoImagen: File): Resource<ReclamoResponse> {
        return try {
            val textType = "text/plain".toMediaTypeOrNull()

            fun toPart(value: String): RequestBody = value.toRequestBody(textType)

            val dataMap = mapOf(
                "PolizaId" to toPart(request.polizaId),
                "UsuarioId" to toPart(request.usuarioId.toString()),
                "Descripcion" to toPart(request.descripcion),
                "Direccion" to toPart(request.direccion),
                "TipoIncidente" to toPart(request.tipoIncidente),
                "FechaIncidente" to toPart(request.fechaIncidente),
                "NumCuenta" to toPart(request.numCuenta)
            )

            val fileRequest = archivoImagen.asRequestBody("image/*".toMediaTypeOrNull())
            val imagenPart = MultipartBody.Part.createFormData("imagen", archivoImagen.name, fileRequest)

            val response = api.crearReclamo(
                partMap = dataMap,
                imagen = imagenPart
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body)
                } else {
                    Resource.Error("Respuesta vacía del servidor al crear reclamo")
                }
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: errorNetwork)
        }
    }

    suspend fun getReclamos(usuarioId: Int? = null): Resource<List<ReclamoResponse>> {
        return try {
            val response = api.obtenerReclamos(usuarioId)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("La lista de reclamos está vacía")
            } else {
                Resource.Error("HTTP ${response.code()} al obtener reclamos: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: errorNetwork)
        }
    }

    suspend fun getReclamo(id: String): Resource<ReclamoResponse> {
        return try {
            val response = api.obtenerReclamoPorId(id)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Reclamo no encontrado")
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: errorNetwork)
        }
    }

    suspend fun updateEstado(id: String, request: ReclamoUpdateRequest): Resource<ReclamoResponse> {
        return try {
            val response = api.actualizarEstadoReclamo(id, request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body)
                } else {
                    Resource.Error("El servidor no devolvió el reclamo actualizado")
                }
            } else {
                Resource.Error("HTTP ${response.code()} al actualizar estado: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: errorNetwork)
        }
    }
}