package edu.ucne.InsurePal.presentation.admin.adminReclamosVehiculos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.reclamoVehiculo.model.ReclamoVehiculo
import edu.ucne.InsurePal.domain.reclamoVehiculo.useCases.GetReclamoVehiculosUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ListaReclamosAdminViewModel @Inject constructor(
    private val getReclamosUseCase: GetReclamoVehiculosUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ListaReclamosAdminUiState())
    val state = _state.asStateFlow()

    init {
        cargarReclamos()
    }

    fun onEvent(event: ListaReclamosAdminEvent) {
        when (event) {
            ListaReclamosAdminEvent.OnCargar -> cargarReclamos()
            is ListaReclamosAdminEvent.OnReclamoClick -> {
                // La navegación se maneja en la UI con el callback
            }
            ListaReclamosAdminEvent.OnErrorDismiss -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun cargarReclamos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = getReclamosUseCase(null)

            when (result) {
                is Resource.Success -> {
                    val lista = result.data ?: emptyList()
                    // Ordenamos: Pendientes primero, luego por fecha descendente
                    val listaOrdenada = lista.sortedWith(
                        compareBy<ReclamoVehiculo> { it.status != "PENDIENTE" } // false (0) viene antes que true (1)
                            .thenByDescending { it.fechaIncidente }
                    )

                    _state.update {
                        it.copy(
                            isLoading = false,
                            reclamos = listaOrdenada
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(isLoading = false, error = result.message ?: "Error desconocido")
                    }
                }
                is Resource.Loading -> { /* Handled initially */ }
            }
        }
    }
}