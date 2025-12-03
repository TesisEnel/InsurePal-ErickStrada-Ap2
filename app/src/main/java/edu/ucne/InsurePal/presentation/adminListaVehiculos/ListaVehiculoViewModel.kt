package edu.ucne.InsurePal.presentation.adminListaVehiculos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.SeguroVehiculo
import edu.ucne.InsurePal.domain.polizas.vehiculo.useCases.GetAllVehiculosUseCase
import edu.ucne.InsurePal.domain.polizas.vehiculo.useCases.UpdateSeguroUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class VehicleListViewModel @Inject constructor(
    private val getAllVehiculosUseCase: GetAllVehiculosUseCase,
    private val updateVehiculoUseCase: UpdateSeguroUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ListaVehiculoUiState())
    val state: StateFlow<ListaVehiculoUiState> = _state.asStateFlow()

    private var loadVehiclesJob: Job? = null

    init {
        loadVehicles()
    }

    fun onEvent(event: ListaVehiculoEvent) {
        when (event) {
            is ListaVehiculoEvent.OnSearchQueryChange -> {
                _state.update {
                    it.copy(
                        searchQuery = event.query,
                        filteredVehicles = applyFilters(it.vehicles, event.query, it.showPendingOnly)
                    )
                }
            }
            is ListaVehiculoEvent.OnTogglePendingFilter -> {
                _state.update {
                    val newFilterState = !it.showPendingOnly
                    it.copy(
                        showPendingOnly = newFilterState,
                        filteredVehicles = applyFilters(it.vehicles, it.searchQuery, newFilterState)
                    )
                }
            }
            is ListaVehiculoEvent.OnSelectVehicle -> {
                _state.update { it.copy(selectedVehicle = event.vehicle, isDetailVisible = true) }
            }
            is ListaVehiculoEvent.OnUpdateStatus -> {
                updateStatus(event.vehicle, event.newStatus)
            }
            ListaVehiculoEvent.OnDismissDetail -> {
                _state.update { it.copy(selectedVehicle = null, isDetailVisible = false) }
            }
            ListaVehiculoEvent.Refresh -> {
                loadVehicles()
            }
        }
    }

    private fun updateStatus(vehicle: SeguroVehiculo, newStatus: String) {
        val id = vehicle.idPoliza
        if (id.isNullOrBlank()) {
            _state.update { it.copy(errorMessage = "Error: El vehículo no tiene un ID de póliza válido.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

             val updatedVehicle = vehicle.copy(
                status = newStatus,
                imagenVehiculo = null
            )

            val result = updateVehiculoUseCase(id, updatedVehicle)

            when(result) {
                is Resource.Success -> {
                    _state.update { it.copy(isDetailVisible = false, selectedVehicle = null) }
                    loadVehicles()
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Error desconocido al actualizar estado"
                        )
                    }
                }
                is Resource.Loading -> {
                    // Manejado por el estado inicial de la corrutina
                }
            }
        }
    }

    private fun loadVehicles() {
        loadVehiclesJob?.cancel()

        loadVehiclesJob = getAllVehiculosUseCase().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true, errorMessage = null) }
                }
                is Resource.Success -> {
                    val list = result.data ?: emptyList()

                    val sortedList = list.sortedByDescending {
                        it.status == "Cotizando" || it.status.isBlank() || it.status == "Pendiente de aprobación"
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            vehicles = sortedList,
                            filteredVehicles = applyFilters(sortedList, it.searchQuery, it.showPendingOnly)
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun applyFilters(
        list: List<SeguroVehiculo>,
        query: String,
        showPendingOnly: Boolean
    ): List<SeguroVehiculo> {
        var result = list

        if (query.isNotBlank()) {
            result = result.filter {
                (it.idPoliza?.contains(query, ignoreCase = true) == true) ||
                        it.placa.contains(query, ignoreCase = true) ||
                        it.marca.contains(query, ignoreCase = true) ||
                        it.modelo.contains(query, ignoreCase = true)
            }
        }

        if (showPendingOnly) {
            result = result.filter {
                it.status == "Pendiente de aprobación" ||
                        it.status == "Pendiente de pago" ||
                        it.status == "Cotizando" ||
                        it.status.isBlank()
            }
        }

        return result
    }
}