package edu.ucne.InsurePal.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.local.UserPreferences
import edu.ucne.InsurePal.domain.polizas.vehiculo.useCases.ObtenerVehiculosUseCase
import edu.ucne.InsurePal.domain.polizas.vida.useCases.GetSegurosVidaUseCase
import edu.ucne.InsurePal.presentation.home.uiModels.LifePolicyUi
import edu.ucne.InsurePal.presentation.home.uiModels.PolicyUiModel
import edu.ucne.InsurePal.presentation.home.uiModels.VehiclePolicyUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getVehiculosUseCase: ObtenerVehiculosUseCase,
    private val getSegurosVidaUseCase: GetSegurosVidaUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            userPreferences.userId.collectLatest { userId ->
                if (userId != null && userId > 0) {
                    observarPolizas(userId)
                }
            }
        }
    }

    private suspend fun observarPolizas(userId: Int) {
        combine(
            getVehiculosUseCase(userId),
            getSegurosVidaUseCase(userId)
        ) { resultVehiculos, resultVida ->

            val listaCombinada = mutableListOf<PolicyUiModel>()
            var isLoading = false
            var error: String? = null

            when(resultVehiculos) {
                is Resource.Loading -> isLoading = true
                is Resource.Error -> error = resultVehiculos.message
                is Resource.Success -> {
                    val uiModels = resultVehiculos.data?.map { v ->
                        VehiclePolicyUi(
                            id = v.idPoliza ?: "",
                            status = if (v.esPagado) "Activo" else "Pendiente",
                            vehicleModel = "${v.marca} ${v.modelo} ${v.anio}",
                            plate = v.placa
                        )
                    }
                    if (uiModels != null) listaCombinada.addAll(uiModels)
                }
            }

            when(resultVida) {
                is Resource.Loading -> isLoading = true
                is Resource.Error -> if(error == null) error = resultVida.message
                is Resource.Success -> {
                    val uiModels = resultVida.data?.map { v ->
                        LifePolicyUi(
                            id = v.id,
                            status = if (v.esPagado) "Activo" else "Pendiente",
                            insuredName = v.nombresAsegurado,
                            coverageAmount = v.montoCobertura
                        )
                    }
                    if (uiModels != null) listaCombinada.addAll(uiModels)
                }
            }

            Triple(isLoading, error, listaCombinada)

        }.collect { (loading, error, lista) ->
            _state.update {
                it.copy(
                    isLoading = loading,
                    error = error,
                    policies = lista
                )
            }
        }
    }
}