package edu.ucne.InsurePal.data.remote.reclamoVehiculo

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ReclamosApiService {

    @Multipart
    @POST("api/reclamos")
    suspend fun crearReclamo(
        @Part("PolizaId") polizaId: RequestBody,
        @Part("UsuarioId") usuarioId: RequestBody,
        @Part("Descripcion") descripcion: RequestBody,
        @Part("Direccion") direccion: RequestBody,
        @Part("TipoIncidente") tipoIncidente: RequestBody,
        @Part("FechaIncidente") fechaIncidente: RequestBody,
        @Part("NumCuenta") numCuenta: RequestBody,
        @Part imagen: MultipartBody.Part
    ): Response<ReclamoResponse>

    @PUT("api/reclamos/{id}/estado")
    suspend fun actualizarEstadoReclamo(
        @Path("id") reclamoId: String,
        @Body request: ReclamoUpdateRequest
    ): Response<ReclamoResponse>

    @GET("api/reclamos")
    suspend fun obtenerReclamos(
        @Query("usuario_id") usuarioId: Int? = null
    ): Response<List<ReclamoResponse>>


    @GET("api/reclamos/{id}")
    suspend fun obtenerReclamoPorId(
        @Path("id") reclamoId: String
    ): Response<ReclamoResponse>
}