package edu.ucne.InsurePal.domain.polizas.vehiculo.useCases

import edu.ucne.InsurePal.domain.polizas.vehiculo.repository.SeguroVehiculoRepository
import javax.inject.Inject

class GetVehiculoUseCase @Inject constructor(private val repo : SeguroVehiculoRepository) {
    suspend operator fun invoke (id: String) = repo.getVehiculo(id)
}