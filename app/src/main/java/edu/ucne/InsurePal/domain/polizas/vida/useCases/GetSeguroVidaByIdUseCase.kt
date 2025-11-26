package edu.ucne.InsurePal.domain.polizas.vida.useCases

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.polizas.vida.model.SeguroVida
import edu.ucne.InsurePal.domain.polizas.vida.repository.SeguroVidaRepository
import jakarta.inject.Inject

class GetSeguroVidaByIdUseCase @Inject constructor(
    private val repository: SeguroVidaRepository
) {
    suspend operator fun invoke(id: String): Resource<SeguroVida> {
        return repository.getSeguroVidaById(id)
    }
}