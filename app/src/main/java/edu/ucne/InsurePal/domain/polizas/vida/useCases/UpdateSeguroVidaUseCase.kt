package edu.ucne.InsurePal.domain.polizas.vida.useCases

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.polizas.vida.model.SeguroVida
import edu.ucne.InsurePal.domain.polizas.vida.repository.SeguroVidaRepository
import jakarta.inject.Inject

class UpdateSeguroVidaUseCase @Inject constructor(
    private val repository: SeguroVidaRepository
) {
    suspend operator fun invoke(id: String, seguroVida: SeguroVida): Resource<Unit> {
        return repository.updateSeguroVida(id, seguroVida)
    }
}