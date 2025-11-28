package edu.ucne.InsurePal.domain.reclamoVehiculo.useCases

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.reclamoVehiculo.model.ReclamoVehiculo
import edu.ucne.InsurePal.domain.reclamoVehiculo.repository.ReclamoVehiculoRepository
import jakarta.inject.Inject

class GetReclamoVehiculosUseCase @Inject constructor(
    private val repository: ReclamoVehiculoRepository
) {
    suspend operator fun invoke(usuarioId: Int? = null): Resource<List<ReclamoVehiculo>> {
        return repository.obtenerReclamoVehiculos(usuarioId)
    }
}