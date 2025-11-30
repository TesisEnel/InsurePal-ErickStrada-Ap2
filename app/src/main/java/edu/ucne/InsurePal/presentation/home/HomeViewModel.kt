package edu.ucne.InsurePal.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.local.UserPreferences
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.SeguroVehiculo
import edu.ucne.InsurePal.domain.polizas.vehiculo.useCases.ObtenerVehiculosUseCase
import edu.ucne.InsurePal.domain.polizas.vida.model.SeguroVida
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

    private val refreshTrigger = MutableStateFlow(0)

    init {
        cargarDatos()
    }

    fun refresh() {
        refreshTrigger.update { it + 1 }
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            combine(
                userPreferences.userId,
                refreshTrigger
            ) { userId, _ ->
                userId
            }.collectLatest { userId ->
                if (userId != null && userId > 0) {
                    observarPolizas(userId)
                } else {
                    _state.update { it.copy(policies = emptyList()) }
                }
            }
        }
    }

    private suspend fun observarPolizas(userId: Int) {
        combine(
            getVehiculosUseCase(userId),
            getSegurosVidaUseCase(userId)
        ) { resultVehiculos, resultVida ->
            processPolicyData(resultVehiculos, resultVida)
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

    private fun processPolicyData(
        resultVehiculos: Resource<List<SeguroVehiculo>>,
        resultVida: Resource<List<SeguroVida>>
    ): Triple<Boolean, String?, List<PolicyUiModel>> {
        val isLoading = resultVehiculos is Resource.Loading || resultVida is Resource.Loading

        val error = resultVehiculos.message ?: resultVida.message

        val listaVehiculos = if (resultVehiculos is Resource.Success) {
            mapVehiclesToUi(resultVehiculos.data)
        } else emptyList()

        val listaVida = if (resultVida is Resource.Success) {
            mapLifeToUi(resultVida.data)
        } else emptyList()

        return Triple(isLoading, error, listaVehiculos + listaVida)
    }


    private fun mapVehiclesToUi(
        data: List<SeguroVehiculo>?
    ): List<PolicyUiModel> {
        return data?.map { v ->
            VehiclePolicyUi(
                id = v.idPoliza ?: "",
                status = v.status,
                vehicleModel = "${v.marca} ${v.modelo} ${v.anio}",
                plate = v.placa
            )
        } ?: emptyList()
    }

    private fun mapLifeToUi(
        data: List<SeguroVida>?
    ): List<PolicyUiModel> {
        return data?.map { v ->
            LifePolicyUi(
                id = v.id,
                status = "Activo",
                insuredName = v.nombresAsegurado,
                coverageAmount = v.montoCobertura
            )
        } ?: emptyList()
    }
}