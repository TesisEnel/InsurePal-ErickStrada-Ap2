package edu.ucne.InsurePal.domain.polizas.vehiculo.useCases

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.toRequest
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.SeguroVehiculo
import edu.ucne.InsurePal.domain.polizas.vehiculo.repository.SeguroVehiculoRepository
import javax.inject.Inject

class SaveSeguroVehiculoUseCase @Inject constructor(
    private val repository: SeguroVehiculoRepository
) {
    suspend operator fun invoke(id: String = "", vehiculo: SeguroVehiculo): Resource<SeguroVehiculo?> {

        val vehiculoRequest = vehiculo.toRequest()
        val result: Resource<SeguroVehiculo?> = if (id.isEmpty()) {
            val postResult = repository.postVehiculo(vehiculoRequest)
            when (postResult) {
                is Resource.Success -> Resource.Success(postResult.data)
                is Resource.Error -> Resource.Error(postResult.message ?: "Error", postResult.data)
                is Resource.Loading -> Resource.Loading(postResult.data)
            }

        } else {
            val putResult = repository.putVehiculo(id, vehiculoRequest)
            when (putResult) {
                is Resource.Success -> Resource.Success(vehiculo)
                is Resource.Error -> Resource.Error(putResult.message ?: "Error al actualizar")
                is Resource.Loading -> Resource.Loading()
            }
        }

        return result
    }
}