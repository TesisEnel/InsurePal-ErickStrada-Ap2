package edu.ucne.InsurePal.domain.pago.useCase

import edu.ucne.InsurePal.domain.pago.repository.PagoRepository
import jakarta.inject.Inject

class SincronizarPagosUseCase @Inject constructor(
    private val repository: PagoRepository
) {
    suspend operator fun invoke(usuarioId: Int) {
         repository.sincronizarPagos(usuarioId)
    }
}