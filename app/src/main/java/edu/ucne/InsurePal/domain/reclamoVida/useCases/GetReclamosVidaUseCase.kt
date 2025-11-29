package edu.ucne.InsurePal.domain.reclamoVida.useCases

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.reclamoVida.model.ReclamoVida
import edu.ucne.InsurePal.domain.reclamoVida.repository.ReclamoVidaRepository
import jakarta.inject.Inject

class GetReclamosVidaUseCase @Inject constructor(
    private val repository: ReclamoVidaRepository
) {
    suspend operator fun invoke(usuarioId: Int? = null): Resource<List<ReclamoVida>> {
        return repository.obtenerReclamosVida(usuarioId)
    }
}