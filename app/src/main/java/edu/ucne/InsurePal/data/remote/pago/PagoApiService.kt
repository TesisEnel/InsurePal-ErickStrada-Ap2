package edu.ucne.InsurePal.data.remote.pago

import edu.ucne.InsurePal.data.remote.pago.dto.HistorialPagoDto
import edu.ucne.InsurePal.data.remote.pago.dto.PagoRequest
import edu.ucne.InsurePal.data.remote.pago.dto.PagoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PagoApiService {


    @POST("api/Pagos")
    suspend fun procesarPago(
        @Body request: PagoRequest
    ): Response<PagoResponse>

    @GET("api/Pagos/usuario/{usuarioId}")
    suspend fun getHistorialPagos(
        @Path("usuarioId") usuarioId: Int
    ): Response<List<HistorialPagoDto>>
}