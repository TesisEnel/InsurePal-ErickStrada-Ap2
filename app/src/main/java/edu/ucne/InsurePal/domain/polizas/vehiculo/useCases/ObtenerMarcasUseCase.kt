package edu.ucne.InsurePal.domain.polizas.vehiculo.useCases

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.MarcaVehiculo
import edu.ucne.InsurePal.domain.polizas.vehiculo.repository.SeguroVehiculoRepository
import jakarta.inject.Inject

class ObtenerMarcasUseCase @Inject constructor(
    private val repository: SeguroVehiculoRepository
) {
    suspend operator fun invoke(): Resource<List<MarcaVehiculo>> {
        return repository.getMarcas()
    }
}