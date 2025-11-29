package edu.ucne.InsurePal.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.polizas.vehiculo.useCases.GetAllVehiculosUseCase
import edu.ucne.InsurePal.domain.polizas.vida.useCases.GetAllSegurosVidaUseCase
import edu.ucne.InsurePal.domain.reclamoVehiculo.useCases.GetReclamoVehiculosUseCase
import edu.ucne.InsurePal.domain.reclamoVida.useCases.GetReclamosVidaUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val getAllVehiculosUseCase: GetAllVehiculosUseCase,
    private val getAllSegurosVidaUseCase: GetAllSegurosVidaUseCase,
    private val getReclamoVehiculosUseCase: GetReclamoVehiculosUseCase,
    private val getReclamosVidaUseCase: GetReclamosVidaUseCase
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
            AdminEvent.OnDismissDetail -> {}
            is AdminEvent.OnSelectPolicy -> {}
        }
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

             val flowReclamosV = flow { emit(getReclamoVehiculosUseCase(null)) }
            val flowReclamosL = flow { emit(getReclamosVidaUseCase(null)) }

            combine(
                getAllVehiculosUseCase(),
                getAllSegurosVidaUseCase(),
                flowReclamosV,
                flowReclamosL
            ) { resVehiculos, resVida, resRecVehiculos, resRecVida ->

                val vehiculos = resVehiculos.data ?: emptyList()
                val vida = resVida.data ?: emptyList()

                val reclamosV = resRecVehiculos.data ?: emptyList()
                val reclamosL = resRecVida.data ?: emptyList()

                val errorMsg = if (
                    resVehiculos is Resource.Error ||
                    resVida is Resource.Error ||
                    resRecVehiculos is Resource.Error ||
                    resRecVida is Resource.Error
                ) {
                    "Error sincronizando algunos datos del dashboard."
                } else {
                    null
                }

                val totalV = vehiculos.size
                val totalL = vida.size
                val activeV = vehiculos.count { it.esPagado }
                val activeL = vida.count { it.esPagado }

                val totalCoverage = vida.sumOf { it.montoCobertura } +
                        vehiculos.sumOf { it.valorMercado }

                val recVCount = reclamosV.size
                val recLCount = reclamosL.size

                val pendingRecV = reclamosV.count { it.status == "PENDIENTE" }
                val pendingRecL = reclamosL.count { it.status == "PENDIENTE" }

                AdminUiState(
                    isLoading = false,
                    errorMessage = errorMsg,

                    totalPolicies = totalV + totalL,
                    totalVehicles = totalV,
                    totalLife = totalL,
                    activeCount = activeV + activeL,
                    pendingCount = (totalV - activeV) + (totalL - activeL),
                    totalCoverageValue = totalCoverage,

                    totalClaims = recVCount + recLCount,
                    vehicleClaimsCount = recVCount,
                    lifeClaimsCount = recLCount,
                    pendingClaimsCount = pendingRecV + pendingRecL
                )

            }.catch { e ->
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }.collect { newState ->
                _state.value = newState
            }
        }
    }
}