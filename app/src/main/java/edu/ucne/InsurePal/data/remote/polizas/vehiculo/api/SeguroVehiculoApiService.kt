package edu.ucne.InsurePal.data.remote.polizas.vehiculo.api

import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.SeguroVehiculoRequest
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.SeguroVehiculoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SeguroVehiculoApiService {
    @GET("api/Vehiculos")
    suspend fun getVehiculos(): Response<List<SeguroVehiculoResponse>>

    @GET("api/Vehiculos/{id}")
    suspend fun getVehiculo(@Path("id") id: Int?): Response<SeguroVehiculoResponse>

    @POST("api/Vehiculos")
    suspend fun postVehiculo(@Body seguroVehiculo: SeguroVehiculoRequest): Response<SeguroVehiculoResponse>

    @PUT("api/Vehiculos/{id}")
    suspend fun putVehiculo(@Path("id") id:Int, @Body seguroVehiculo: SeguroVehiculoRequest): Response<Unit>

}