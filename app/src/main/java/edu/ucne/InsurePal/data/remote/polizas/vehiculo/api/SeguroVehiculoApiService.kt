package edu.ucne.InsurePal.data.remote.polizas.vehiculo.api

import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.MarcaVehiculoDto
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.SeguroVehiculoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path

interface SeguroVehiculoApiService {
    @GET("api/Marcas")
    suspend fun getMarcas(): Response<List<MarcaVehiculoDto>>

    @GET("api/Vehiculos")
    suspend fun getAllVehiculos(): Response<List<SeguroVehiculoResponse>>

    @GET("api/Vehiculos/Usuario/{usuarioId}")
    suspend fun getVehiculos(@Path("usuarioId") usuarioId: Int): Response<List<SeguroVehiculoResponse>>

    @GET("api/Vehiculos/{id}")
    suspend fun getVehiculo(@Path("id") id: String?): Response<SeguroVehiculoResponse>

    @Multipart
    @POST("api/Vehiculos")
    suspend fun postVehiculo(
        @Part imagen: MultipartBody.Part?,

        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>
    ): Response<SeguroVehiculoResponse>

    @Multipart
    @PUT("api/Vehiculos/{id}")
    suspend fun putVehiculo(
        @Path("id") id: String?,
        @Part imagen: MultipartBody.Part?,
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>
    ): Response<Unit>
    @DELETE("api/Vehiculos/{id}")
    suspend fun deleteVehiculo(@Path("id") id: String): Response<Unit>
}