package edu.ucne.InsurePal.presentation.detallePoliza

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.local.UserPreferences
import edu.ucne.InsurePal.domain.polizas.vehiculo.repository.SeguroVehiculoRepository
import edu.ucne.InsurePal.domain.polizas.vehiculo.useCases.CalcularPrimaUseCase
import edu.ucne.InsurePal.domain.polizas.vehiculo.useCases.EliminarSeguroVehiculoUseCase
import edu.ucne.InsurePal.domain.polizas.vehiculo.useCases.GetVehiculoUseCase
import edu.ucne.InsurePal.domain.polizas.vida.useCases.DeleteSeguroVidaUseCase
import edu.ucne.InsurePal.domain.polizas.vida.useCases.GetSeguroVidaByIdUseCase
import edu.ucne.InsurePal.presentation.polizas.vehiculo.cotizacionVehiculo.formatearMoneda
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetallePolizaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getVehiculoUseCase: GetVehiculoUseCase,
    private val eliminarVehiculoUseCase: EliminarSeguroVehiculoUseCase,
    private val vehiculoRepository: SeguroVehiculoRepository,
    private val getVidaUseCase: GetSeguroVidaByIdUseCase,
    private val eliminarVidaUseCase: DeleteSeguroVidaUseCase,
    private val calcularPrimaUseCase: CalcularPrimaUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(DetallePolizaUiState())
    val state = _state.asStateFlow()

    private val policyId: String = savedStateHandle["policyId"] ?: ""
    val policyType: String = savedStateHandle["policyType"] ?: ""

    init {
        viewModelScope.launch {
            userPreferences.userId.collect { id ->
                _state.update { it.copy(usuarioId = id?: 0) }
            }
        }
        cargarDetalle()
    }

    fun onEvent(event: DetallePolizaEvent) {
        when(event) {
            DetallePolizaEvent.OnEliminarPoliza -> eliminarPoliza()
            is DetallePolizaEvent.OnCambiarPlanVehiculo -> cambiarPlanVehiculo(event.nuevoPlan)
            DetallePolizaEvent.OnErrorDismiss -> _state.update { it.copy(error = null) }
        }
    }

    private fun cargarDetalle() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            if (policyType == "VEHICULO") {
                cargarVehiculo()
            } else {
                cargarVida()
            }
        }
    }

    private suspend fun cargarVehiculo() {
        val result = getVehiculoUseCase(policyId)

        when(result) {
            is Resource.Success -> {
                val v = result.data
                if (v != null) {
                    val desglose = calcularPrimaUseCase(v.valorMercado, v.coverageType)
                    val precioTotal = desglose.total

                    _state.update {
                        it.copy(
                            isLoading = false,
                            policyId = v.idPoliza ?: policyId,
                            usuarioId = v.usuarioId,
                            title = "${v.marca} ${v.modelo}",
                            subtitle = "${v.anio} • ${v.color}",
                            status = v.status,
                            price = precioTotal,
                            isPaid = v.esPagado,
                            coverageType = v.coverageType,
                            details = mapOf(
                                "Placa" to v.placa,
                                "Chasis" to v.chasis,
                                "Tipo Cobertura" to v.coverageType,
                                "Valor Vehículo" to "RD$ ${formatearMoneda(v.valorMercado)}",
                                "Próximo Pago" to (v.fechaPago ?: "Pendiente"),
                            )
                        )
                    }
                }
            }
            is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
            is Resource.Loading -> _state.update { it.copy(isLoading = true) }
        }
    }

    private suspend fun cargarVida() {
        val result = getVidaUseCase(policyId)

        when(result) {
            is Resource.Success -> {
                val v = result.data!!
                _state.update {
                    it.copy(
                        isLoading = false,
                        policyId = "VIDA-${v.id}",
                        usuarioId = v.usuarioId,
                        title = v.nombresAsegurado,
                        subtitle = "Seguro de Vida",
                        status = if(v.esPagado) "Activo" else "Pendiente",
                        price = v.prima,
                        isPaid = v.esPagado,
                        details = mapOf(
                            "Beneficiario" to v.nombreBeneficiario,
                            "Cédula Beneficiario" to v.cedulaBeneficiario,
                            "Monto Cobertura" to "RD$ ${v.montoCobertura}",
                            "Ocupación" to v.ocupacion,
                            "Próximo Pago" to (v.fechaPago ?: "Pendiente"),
                        )
                    )
                }
            }
            is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
            is Resource.Loading -> _state.update { it.copy(isLoading = true) }
        }
    }

    private fun eliminarPoliza() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = if (policyType == "VEHICULO") {
                try {
                    eliminarVehiculoUseCase(policyId)
                } catch (e: Exception) {
                    Resource.Error("Error al eliminar: ${e.message}")
                }
            } else {
                eliminarVidaUseCase(policyId)
            }

            if (result is Resource.Success<*>) {
                _state.update { it.copy(isLoading = false, isDeleted = true) }
            } else {
                _state.update { it.copy(isLoading = false, error = result.message ?: "No se pudo eliminar la póliza") }
            }
        }
    }

    private fun cambiarPlanVehiculo(nuevoPlan: String) {
        if (policyType != "VEHICULO") return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val resultGet = vehiculoRepository.getVehiculo(policyId)

            if (resultGet is Resource.Success<*>) {
                val vehiculo = resultGet.data

                if (vehiculo != null) {
                    val vehiculoActualizado = vehiculo.copy(coverageType = nuevoPlan)

                    val resultUpdate = vehiculoRepository.putVehiculo(policyId, vehiculoActualizado)

                    if (resultUpdate is Resource.Success<*>) {
                        cargarVehiculo()
                    } else {
                        _state.update { it.copy(isLoading = false, error = resultUpdate.message) }
                    }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Error: Datos de vehículo corruptos") }
                }
            } else {
                _state.update { it.copy(isLoading = false, error = resultGet.message) }
            }
        }
    }
}