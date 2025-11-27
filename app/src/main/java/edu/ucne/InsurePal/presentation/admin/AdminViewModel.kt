package edu.ucne.InsurePal.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.polizas.vehiculo.useCases.GetAllVehiculosUseCase
import edu.ucne.InsurePal.domain.polizas.vida.useCases.GetAllSegurosVidaUseCase
import edu.ucne.InsurePal.presentation.home.uiModels.LifePolicyUi
import edu.ucne.InsurePal.presentation.home.uiModels.PolicyUiModel
import edu.ucne.InsurePal.presentation.home.uiModels.VehiclePolicyUi
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
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
        loadData()
    }

    fun onEvent(event: AdminEvent) {
        when(event) {
            AdminEvent.LoadDashboard -> loadData()

            is AdminEvent.OnSelectPolicy -> {
                val policy = _state.value.policies.find { it.id == event.policyId }
                _state.update { it.copy(selectedPolicy = policy, isDetailVisible = true) }
            }

            AdminEvent.OnDismissDetail -> {
                _state.update { it.copy(selectedPolicy = null, isDetailVisible = false) }
            }

            AdminEvent.OnLogout -> { }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                getAllVehiculosUseCase(),
                getAllSegurosVidaUseCase()
            ) { resVehiculos, resVida ->

                val listaCombinada = mutableListOf<PolicyUiModel>()
                var isLoading = false
                var error: String? = null

                when(resVehiculos) {
                    is Resource.Loading -> isLoading = true
                    is Resource.Error -> {
                        error = "Error Vehículos: ${resVehiculos.message}"
                    }
                    is Resource.Success -> {
                        val data = resVehiculos.data ?: emptyList()
                        val uiModels = data.map { v ->
                            VehiclePolicyUi(
                                id = "${v.idPoliza}",
                                status = if (v.esPagado) "Activo" else "Pendiente",
                                vehicleModel = "${v.marca} ${v.modelo}",
                                plate = v.placa
                            )
                        }
                        listaCombinada.addAll(uiModels)
                    }
                }
                when(resVida) {
                    is Resource.Loading -> isLoading = true
                    is Resource.Error -> {
                        val msg = "Error Vida: ${resVida.message}"
                        error = if (error == null) msg else "$error | $msg"
                    }
                    is Resource.Success -> {
                        val data = resVida.data ?: emptyList()
                        val uiModels = data.map { v ->
                            LifePolicyUi(
                                id = v.id,
                                status = if (v.esPagado) "Activo" else "Pendiente",
                                insuredName = v.nombresAsegurado,
                                coverageAmount = v.montoCobertura ?: 0.0,
                            )
                        }
                        listaCombinada.addAll(uiModels)
                    }
                }

                Triple(isLoading, error, listaCombinada)
            }
                .onStart {
                    _state.update { it.copy(isLoading = true) }
                }
                .catch { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { (loading, error, policies) ->
                    _state.update {
                        it.copy(
                            isLoading = loading,
                            errorMessage = error,
                            policies = policies
                        )
                    }
                }
        }
    }
}