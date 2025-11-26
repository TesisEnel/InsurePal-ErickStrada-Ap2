package edu.ucne.InsurePal.data.remote.pago


import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.remote.pago.dto.HistorialPagoDto
import edu.ucne.InsurePal.data.remote.pago.dto.PagoRequest
import edu.ucne.InsurePal.data.remote.pago.dto.PagoResponse
import javax.inject.Inject

class PagoRemoteDataSource @Inject constructor(
    private val api: PagoApiService
) {
    private val errorDeRed = "No se pudo conectar con el servidor de pagos"

    suspend fun procesarPago(request: PagoRequest): Resource<PagoResponse> {
        return try {
            val response = api.procesarPago(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    if (body.exito) {
                        Resource.Success(body)
                    } else {
                        Resource.Error(body.mensaje)
                    }
                } else {
                    Resource.Error("Respuesta vacía del servidor de pagos")
                }
            } else {
                Resource.Error("Error HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: errorDeRed)
        }
    }


    suspend fun getHistorialRemoto(usuarioId: Int): Resource<List<HistorialPagoDto>> {
        return try {
            val response = api.getHistorialPagos(usuarioId)

            if (response.isSuccessful) {
                response.body()?.let { lista ->
                    Resource.Success(lista)
                } ?: Resource.Error("La lista de pagos vino vacía")
            } else {
                Resource.Error("Error HTTP ${response.code()} al sincronizar pagos")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de conexión al sincronizar historial")
        }
    }
}