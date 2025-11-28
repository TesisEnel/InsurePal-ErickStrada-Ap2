package edu.ucne.InsurePal.domain.reclamoVehiculo.useCases

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.reclamoVehiculo.model.ReclamoVehiculo
import edu.ucne.InsurePal.domain.reclamoVehiculo.repository.ReclamoVehiculoRepository
import jakarta.inject.Inject

class CambiarEstadoReclamoUseCase @Inject constructor(
    private val repository: ReclamoVehiculoRepository
) {
    suspend operator fun invoke(
        reclamoId: String,
        nuevoEstado: String,
        motivoRechazo: String?
    ): Resource<ReclamoVehiculo> {

        if (nuevoEstado == "RECHAZADO" && motivoRechazo.isNullOrBlank()) {
            return Resource.Error("Para rechazar un reclamo debes especificar el motivo.")
        }

        return repository.cambiarEstadoReclamoVehiculo(reclamoId, nuevoEstado, motivoRechazo)
    }
}