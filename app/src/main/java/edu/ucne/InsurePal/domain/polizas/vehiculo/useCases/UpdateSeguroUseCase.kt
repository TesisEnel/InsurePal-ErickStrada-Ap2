package edu.ucne.InsurePal.domain.polizas.vehiculo.useCases

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.SeguroVehiculo
import edu.ucne.InsurePal.domain.polizas.vehiculo.repository.SeguroVehiculoRepository
import javax.inject.Inject

class UpdateSeguroUseCase @Inject constructor(
    private val repository: SeguroVehiculoRepository
) {
    suspend operator fun invoke(id: String?, vehiculo: SeguroVehiculo): Resource<Unit> {
        return repository.putVehiculo(id, vehiculo)
    }
}