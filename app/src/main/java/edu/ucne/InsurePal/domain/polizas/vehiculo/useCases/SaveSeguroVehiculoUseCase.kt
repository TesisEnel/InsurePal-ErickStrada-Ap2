package edu.ucne.InsurePal.domain.polizas.vehiculo.useCases

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.SeguroVehiculo
import edu.ucne.InsurePal.domain.polizas.vehiculo.repository.SeguroVehiculoRepository
import javax.inject.Inject

class SaveSeguroVehiculoUseCase @Inject constructor(
    private val repository: SeguroVehiculoRepository
) {
    suspend operator fun invoke(id: String = "", vehiculo: SeguroVehiculo): Resource<SeguroVehiculo> {
        return if (id.isBlank()) {
            repository.postVehiculo(vehiculo)
        } else {
            val result = repository.putVehiculo(id, vehiculo)

            when(result) {
                is Resource.Success -> Resource.Success(vehiculo)
                is Resource.Error -> Resource.Error(result.message ?: "Error al actualizar")
                is Resource.Loading -> Resource.Loading()
            }
        }
    }
}