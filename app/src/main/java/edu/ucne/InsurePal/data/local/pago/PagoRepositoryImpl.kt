package edu.ucne.InsurePal.data.local.pago

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.local.UserPreferences
import edu.ucne.InsurePal.data.remote.pago.PagoRemoteDataSource
import edu.ucne.InsurePal.data.remote.pago.dto.PagoRequest
import edu.ucne.InsurePal.data.toDomain
import edu.ucne.InsurePal.domain.pago.model.Pago
import edu.ucne.InsurePal.domain.pago.model.TarjetaCredito
import edu.ucne.InsurePal.domain.pago.repository.PagoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class PagoRepositoryImpl @Inject constructor(
    private val remoteDataSource: PagoRemoteDataSource,
    private val pagoDao: PagoDao,
    private val userPreferences: UserPreferences
) : PagoRepository {

    override fun getHistorialPagos(usuarioId: Int): Flow<List<Pago>> {
        return pagoDao.getPagosPorUsuario(usuarioId).map { entities ->
            entities.map { it.toDomain() }
        }
    }


    override suspend fun procesarPago(
        polizaId: String,
        monto: Double,
        tarjeta: TarjetaCredito
    ): Resource<Pago> {

        val usuarioIdActual = userPreferences.userId.first() ?: 0

        val request = PagoRequest(
            polizaId = polizaId,
            usuarioId = usuarioIdActual,
            monto = monto,
            numeroTarjeta = tarjeta.numero,
            cvv = tarjeta.cvv,
            fechaVencimiento = tarjeta.fechaVencimiento,
            titular = tarjeta.titular
        )

        val resultadoApi = remoteDataSource.procesarPago(request)

        return when(resultadoApi) {
            is Resource.Success -> {
                val data = resultadoApi.data!!

                 val nuevaEntity = PagoEntity(
                    polizaId = polizaId,
                    usuarioId = usuarioIdActual,
                    fechaIso = LocalDateTime.now().toString(),
                    monto = monto,
                    tarjetaMascara = "**** ${tarjeta.numero.takeLast(4)}",
                    estado = "APROBADO",
                    numeroConfirmacion = data.numeroTransaccion ?: "N/A"
                )

                pagoDao.insertPago(nuevaEntity)

                Resource.Success(nuevaEntity.toDomain())
            }
            is Resource.Error -> {
                Resource.Error(resultadoApi.message ?: "Error al procesar el pago")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }
    override suspend fun sincronizarPagos(usuarioId: Int) {
        val result = remoteDataSource.getHistorialRemoto(usuarioId)

        if (result is Resource.Success) {
            val listaDto = result.data ?: emptyList()

            val entities = listaDto.map { dto ->
                PagoEntity(
                    id = dto.id,
                    polizaId = dto.polizaId,
                    usuarioId = usuarioId,
                    fechaIso = dto.fecha,
                    monto = dto.monto,
                    tarjetaMascara = dto.tarjetaMascara,
                    estado = dto.estado,
                    numeroConfirmacion = dto.numeroConfirmacion
                )
            }

            if (entities.isNotEmpty()) {
                pagoDao.insertAll(entities)
            }
        }
    }
}