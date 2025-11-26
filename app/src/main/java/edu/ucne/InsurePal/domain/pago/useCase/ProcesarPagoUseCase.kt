package edu.ucne.InsurePal.domain.pago.useCase

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.pago.model.Pago
import edu.ucne.InsurePal.domain.pago.model.TarjetaCredito
import edu.ucne.InsurePal.domain.pago.repository.PagoRepository

import javax.inject.Inject

class ProcesarPagoUseCase @Inject constructor(
    private val repository: PagoRepository
) {

    suspend operator fun invoke(
        polizaId: String,
        monto: Double,
        tarjeta: TarjetaCredito
    ): Resource<Pago> {
        if (monto <= 0) {
            return Resource.Error("El monto a pagar debe ser mayor que 0.")
        }
        return repository.procesarPago(polizaId, monto, tarjeta)
    }
}