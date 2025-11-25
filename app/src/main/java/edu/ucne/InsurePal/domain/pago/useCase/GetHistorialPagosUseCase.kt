package edu.ucne.InsurePal.domain.pago.useCase

import edu.ucne.InsurePal.domain.pago.model.Pago
import edu.ucne.InsurePal.domain.pago.repository.PagoRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetHistorialPagosUseCase @Inject constructor(
    private val repository: PagoRepository
) {

    operator fun invoke(usuarioId: Int): Flow<List<Pago>> {
        return repository.getHistorialPagos(usuarioId)
    }
}