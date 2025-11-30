package edu.ucne.InsurePal.domain.reclamoVehiculo.useCases

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.reclamoVehiculo.model.CrearReclamoVehiculoParams
import edu.ucne.InsurePal.domain.reclamoVehiculo.model.ReclamoVehiculo
import edu.ucne.InsurePal.domain.reclamoVehiculo.repository.ReclamoVehiculoRepository
import jakarta.inject.Inject
class CrearReclamoVehiculoUseCase @Inject constructor(
    private val repository: ReclamoVehiculoRepository
) {
    suspend operator fun invoke(params: CrearReclamoVehiculoParams): Resource<ReclamoVehiculo> {
        return repository.crearReclamoVehiculo(params)
    }
}