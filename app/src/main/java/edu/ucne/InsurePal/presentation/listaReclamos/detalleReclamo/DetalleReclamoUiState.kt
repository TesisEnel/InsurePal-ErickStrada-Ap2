package edu.ucne.InsurePal.presentation.listaReclamos.detalleReclamo

import edu.ucne.InsurePal.domain.reclamoVehiculo.model.ReclamoVehiculo
import edu.ucne.InsurePal.domain.reclamoVida.model.ReclamoVida
import edu.ucne.InsurePal.presentation.listaReclamos.UiModels.TipoReclamo

data class DetalleReclamoUiState(
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val exitoOperacion: String? = null,
    val error: String? = null,
    val tipo: TipoReclamo = TipoReclamo.VEHICULO,
    val reclamoVehiculo: ReclamoVehiculo? = null,
     val reclamoVida: ReclamoVida? = null
)