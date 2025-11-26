package edu.ucne.InsurePal.domain.polizas.vehiculo.useCases

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.SeguroVehiculo
import edu.ucne.InsurePal.domain.polizas.vehiculo.repository.SeguroVehiculoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerVehiculosUseCase @Inject constructor(
    private val repository: SeguroVehiculoRepository
) {
    suspend operator fun invoke(usuarioId : Int): Flow<Resource<List<SeguroVehiculo>>> = repository.getVehiculos(usuarioId)
}