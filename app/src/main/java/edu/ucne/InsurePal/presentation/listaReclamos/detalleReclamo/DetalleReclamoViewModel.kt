package edu.ucne.InsurePal.presentation.listaReclamos.detalleReclamo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.reclamoVehiculo.useCases.CambiarEstadoReclamoUseCase
import edu.ucne.InsurePal.domain.reclamoVehiculo.useCases.GetReclamoVehiculoByIdUseCase
import edu.ucne.InsurePal.domain.reclamoVida.useCases.CambiarEstadoReclamoVidaUseCase
import edu.ucne.InsurePal.domain.reclamoVida.useCases.GetReclamoVidaByIdUseCase
import edu.ucne.InsurePal.presentation.listaReclamos.UiModels.TipoReclamo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DetalleReclamoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getReclamoVehiculoUseCase: GetReclamoVehiculoByIdUseCase,
    private val cambiarEstadoVehiculoUseCase: CambiarEstadoReclamoUseCase,

    private val getReclamoVidaUseCase: GetReclamoVidaByIdUseCase,
    private val cambiarEstadoVidaUseCase: CambiarEstadoReclamoVidaUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DetalleReclamoUiState())
    val state = _state.asStateFlow()

    private val reclamoId: String = savedStateHandle.get<String>("reclamoId") ?: ""

    init {
        detectarTipoYCargar()
    }

    fun onEvent(event: DetalleReclamoEvent) {
        when(event) {
            DetalleReclamoEvent.OnErrorDismiss -> _state.update { it.copy(error = null) }
            DetalleReclamoEvent.OnReintentar -> detectarTipoYCargar()

            DetalleReclamoEvent.OnAprobar -> cambiarEstado("APROBADO", null)
            is DetalleReclamoEvent.OnRechazar -> cambiarEstado("RECHAZADO", event.motivo)
        }
    }

    private fun detectarTipoYCargar() {
        if (reclamoId.isBlank()) return

        if (reclamoId.startsWith("VIDA-")) {
            _state.update { it.copy(tipo = TipoReclamo.VIDA) }
            cargarReclamoVida()
        } else {
            _state.update { it.copy(tipo = TipoReclamo.VEHICULO) }
            cargarReclamoVehiculo()
        }
    }

    private fun cargarReclamoVehiculo() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = getReclamoVehiculoUseCase(reclamoId)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(isLoading = false, reclamoVehiculo = result.data)
                    }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> { }
            }
        }
    }

    private fun cargarReclamoVida() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getReclamoVidaUseCase(reclamoId)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(isLoading = false, reclamoVida = result.data)
                    }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> { }
            }
        }
    }

    private fun cambiarEstado(nuevoEstado: String, motivo: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isUpdating = true) }

            val currentState = _state.value

            if (currentState.tipo == TipoReclamo.VEHICULO) {
                val id = currentState.reclamoVehiculo?.id ?: return@launch
                val result = cambiarEstadoVehiculoUseCase(id, nuevoEstado, motivo)
                procesarResultadoUpdate(result, nuevoEstado)
            } else {
                val id = currentState.reclamoVida?.id ?: return@launch
                val result = cambiarEstadoVidaUseCase(id, nuevoEstado, motivo)
                procesarResultadoUpdate(result, nuevoEstado)
            }
        }
    }

    private fun <T> procesarResultadoUpdate(result: Resource<T>, nuevoEstado: String) {
        when (result) {
            is Resource.Success -> {
                if (_state.value.tipo == TipoReclamo.VEHICULO) cargarReclamoVehiculo() else cargarReclamoVida()

                _state.update {
                    it.copy(
                        isUpdating = false,
                        exitoOperacion = "Reclamo $nuevoEstado correctamente"
                    )
                }
            }
            is Resource.Error -> {
                _state.update { it.copy(isUpdating = false, error = result.message) }
            }
            else -> {}
        }
    }
}