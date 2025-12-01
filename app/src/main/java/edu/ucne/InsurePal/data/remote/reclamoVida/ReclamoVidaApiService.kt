package edu.ucne.InsurePal.data.remote.reclamoVida

import edu.ucne.InsurePal.data.remote.reclamoVida.dto.ReclamoVidaResponse
import edu.ucne.InsurePal.data.remote.reclamoVida.dto.ReclamoVidaUpdateRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ReclamoVidaApiService {

    @Multipart
    @POST("api/reclamos-vida")
    suspend fun crearReclamoVida(
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,

        @Part actaDefuncion: MultipartBody.Part,

        @Part identificacion: MultipartBody.Part? = null
    ): Response<ReclamoVidaResponse>

    @PUT("api/reclamos-vida/{id}/estado")
    suspend fun actualizarEstadoReclamo(
        @Path("id") reclamoId: String,
        @Body request: ReclamoVidaUpdateRequest
    ): Response<ReclamoVidaResponse>


    @GET("api/reclamos-vida")
    suspend fun obtenerReclamos(
        @Query("usuarioId") usuarioId: Int? = null
    ): Response<List<ReclamoVidaResponse>>

    @GET("api/reclamos-vida/{id}")
    suspend fun obtenerReclamoPorId(
        @Path("id") reclamoId: String
    ): Response<ReclamoVidaResponse>
}