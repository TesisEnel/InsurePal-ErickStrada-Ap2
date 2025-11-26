package edu.ucne.InsurePal.domain.polizas.vehiculo.useCases

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.polizas.vehiculo.repository.SeguroVehiculoRepository
import javax.inject.Inject

class EliminarSeguroVehiculoUseCase @Inject constructor(
    private val repo: SeguroVehiculoRepository
) {
    suspend operator fun invoke(id: String): Resource<Unit> = repo.delete(id)
}