package edu.ucne.InsurePal.data.remote.reclamoVida

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.remote.reclamoVida.dto.ReclamoVidaCreateRequest
import edu.ucne.InsurePal.data.remote.reclamoVida.dto.ReclamoVidaResponse
import edu.ucne.InsurePal.data.remote.reclamoVida.dto.ReclamoVidaUpdateRequest
import jakarta.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ReclamoVidaRemoteDataSource @Inject constructor(
    private val api: ReclamoVidaApiService
) {
    private val errorNetwork = "Error de conexión con el servidor"

    suspend fun crearReclamoVida(
        request: ReclamoVidaCreateRequest,
        archivoActa: File,
        archivoIdentificacion: File?
    ): Resource<ReclamoVidaResponse> {
        return try {
            val textType = "text/plain".toMediaTypeOrNull()

            fun toPart(value: String): RequestBody = value.toRequestBody(textType)

            val dataMap = mapOf(
                "PolizaId" to toPart(request.polizaId),
                "UsuarioId" to toPart(request.usuarioId.toString()),
                "NombreAsegurado" to toPart(request.nombreAsegurado),
                "Descripcion" to toPart(request.descripcion),
                "LugarFallecimiento" to toPart(request.lugarFallecimiento),
                "CausaMuerte" to toPart(request.causaMuerte),
                "FechaFallecimiento" to toPart(request.fechaFallecimiento),
                "NumCuenta" to toPart(request.numCuenta)
            )

            val actaRequest = archivoActa.asRequestBody("image/*".toMediaTypeOrNull())
            val actaPart = MultipartBody.Part.createFormData("ActaDefuncion", archivoActa.name, actaRequest)

            var idPart: MultipartBody.Part? = null
            if (archivoIdentificacion != null && archivoIdentificacion.exists()) {
                val idRequest = archivoIdentificacion.asRequestBody("image/*".toMediaTypeOrNull())
                idPart = MultipartBody.Part.createFormData("Identificacion", archivoIdentificacion.name, idRequest)
            }

            val response = api.crearReclamoVida(
                data = dataMap,
                actaDefuncion = actaPart,
                identificacion = idPart
            )

            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: errorNetwork)
        }
    }

    suspend fun getReclamos(usuarioId: Int? = null): Resource<List<ReclamoVidaResponse>> {
        return try {
            val response = api.obtenerReclamos(usuarioId)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("La lista de reclamos de vida está vacía")
            } else {
                Resource.Error("HTTP ${response.code()} al obtener reclamos: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: errorNetwork)
        }
    }

    suspend fun getReclamo(id: String): Resource<ReclamoVidaResponse> {
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

    suspend fun updateEstado(id: String, request: ReclamoVidaUpdateRequest): Resource<ReclamoVidaResponse> {
        return try {
            val response = api.actualizarEstadoReclamo(id, request)
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("El servidor no devolvió el reclamo actualizado")
            } else {
                Resource.Error("HTTP ${response.code()} al actualizar estado: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: errorNetwork)
        }
    }
}