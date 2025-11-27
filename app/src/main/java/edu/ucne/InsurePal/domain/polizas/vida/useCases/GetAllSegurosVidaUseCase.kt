package edu.ucne.InsurePal.domain.polizas.vida.useCases

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.polizas.vida.model.SeguroVida
import edu.ucne.InsurePal.domain.polizas.vida.repository.SeguroVidaRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAllSegurosVidaUseCase @Inject constructor(
    private val repository: SeguroVidaRepository
) {
    operator fun invoke(): Flow<Resource<List<SeguroVida>>> {
        return repository.getAllSegurosVida()
    }
}