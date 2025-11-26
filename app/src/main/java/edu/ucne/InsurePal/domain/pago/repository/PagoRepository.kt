package edu.ucne.InsurePal.domain.pago.repository

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.pago.model.Pago
import edu.ucne.InsurePal.domain.pago.model.TarjetaCredito
import kotlinx.coroutines.flow.Flow

interface PagoRepository {
    fun getHistorialPagos(usuarioId: Int): Flow<List<Pago>>

    suspend fun procesarPago(
        polizaId: String,
        monto: Double,
        tarjeta: TarjetaCredito
    ): Resource<Pago>

    suspend fun sincronizarPagos(usuarioId: Int)
}