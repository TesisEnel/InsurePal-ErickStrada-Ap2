package edu.ucne.InsurePal.domain.polizas.vehiculo.useCases

import edu.ucne.InsurePal.domain.polizas.vehiculo.model.MarcaVehiculo
import edu.ucne.InsurePal.domain.polizas.vehiculo.repository.VehiculoRepository
import jakarta.inject.Inject

class ObtenerMarcasUseCase @Inject constructor(
    private val repository: VehiculoRepository
) {
    operator fun invoke(): List<MarcaVehiculo> {
        return repository.getMarcas()
    }
}