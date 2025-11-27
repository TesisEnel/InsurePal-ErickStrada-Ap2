package edu.ucne.InsurePal.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.polizas.vehiculo.useCases.GetAllVehiculosUseCase
import edu.ucne.InsurePal.domain.polizas.vida.useCases.GetAllSegurosVidaUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val getAllVehiculosUseCase: GetAllVehiculosUseCase,
    private val getAllSegurosVidaUseCase: GetAllSegurosVidaUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AdminUiState())
    val state: StateFlow<AdminUiState> = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    fun onEvent(event: AdminEvent) {
        when(event) {
            AdminEvent.LoadDashboard -> loadDashboardData()
            AdminEvent.OnLogout -> {  }
            else -> {}
        }
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            combine(
                getAllVehiculosUseCase(),
                getAllSegurosVidaUseCase()
            ) { resVehiculos, resVida ->

                val vehiculos = resVehiculos.data ?: emptyList()
                val vida = resVida.data ?: emptyList()

                val errorMsg = if (resVehiculos is Resource.Error || resVida is Resource.Error) {
                    "Error cargando algunos datos. Verifica tu conexión."
                } else {
                    null
                }

                val totalV = vehiculos.size
                val totalL = vida.size

                val activeV = vehiculos.count { it.esPagado }
                val activeL = vida.count { it.esPagado }

                val totalCoverage = vida.sumOf { it.montoCobertura ?: 0.0 } +
                        vehiculos.sumOf { it.valorMercado }
                AdminUiState(
                    isLoading = false,
                    errorMessage = errorMsg,
                    totalPolicies = totalV + totalL,
                    totalVehicles = totalV,
                    totalLife = totalL,
                    activeCount = activeV + activeL,
                    pendingCount = (totalV - activeV) + (totalL - activeL),
                    totalCoverageValue = totalCoverage
                )

            }.catch { e ->
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }.collect { newState ->
                _state.value = newState
            }
        }
    }
}