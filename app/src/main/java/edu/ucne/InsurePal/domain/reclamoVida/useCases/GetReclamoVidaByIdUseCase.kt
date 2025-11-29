package edu.ucne.InsurePal.domain.reclamoVida.useCases

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.reclamoVida.model.ReclamoVida
import edu.ucne.InsurePal.domain.reclamoVida.repository.ReclamoVidaRepository
import jakarta.inject.Inject

class GetReclamoVidaByIdUseCase @Inject constructor(
    private val repository: ReclamoVidaRepository
) {
    suspend operator fun invoke(id: String): Resource<ReclamoVida> {
        return repository.obtenerReclamoVidaPorId(id)
    }
}