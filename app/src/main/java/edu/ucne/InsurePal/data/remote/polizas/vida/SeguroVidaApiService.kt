package edu.ucne.InsurePal.data.remote.polizas.vida

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SeguroVidaApiService {

    @GET("api/SegurosVida/usuario/{usuarioId}")
    suspend fun getSegurosVida(
        @Path("usuarioId") usuarioId: Int
    ): Response<List<SeguroVidaResponse>>

    @GET("api/SegurosVida/{id}")
    suspend fun getSeguroVidaById(@Path("id") id: String): Response<SeguroVidaResponse>

    @POST("api/SegurosVida")
    suspend fun saveSeguroVida(@Body seguro: SeguroVidaRequest): Response<SeguroVidaResponse>

    @PUT("api/SegurosVida/{id}")
    suspend fun updateSeguroVida(
        @Path("id") id: String,
        @Body seguro: SeguroVidaRequest
    ): Response<SeguroVidaResponse>

    @DELETE("api/SegurosVida/{id}")
    suspend fun deleteSeguroVida(@Path("id") id: String): Response<Unit>
}