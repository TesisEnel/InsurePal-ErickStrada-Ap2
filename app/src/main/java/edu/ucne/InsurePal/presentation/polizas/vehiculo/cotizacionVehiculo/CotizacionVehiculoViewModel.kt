package edu.ucne.InsurePal.presentation.polizas.vehiculo.cotizacionVehiculo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.polizas.vehiculo.useCases.CalcularPrimaUseCase
import edu.ucne.InsurePal.domain.polizas.vehiculo.useCases.EliminarSeguroVehiculoUseCase
import edu.ucne.InsurePal.domain.polizas.vehiculo.useCases.GetVehiculoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CotizacionVehiculoViewModel @Inject constructor(
    private val getVehiculoUseCase: GetVehiculoUseCase,
    private val calcularPrimaUseCase: CalcularPrimaUseCase,
    savedStateHandle: SavedStateHandle,
    private val eliminar: EliminarSeguroVehiculoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CotizacionVehiculoUiState())
    val state = _state.asStateFlow()

    private val vehiculoId: String? = savedStateHandle.get<String>("vehiculoId")

    init {
        if (vehiculoId != null) {
            cargarDatosCotizacion(vehiculoId)
        } else {
            _state.update { it.copy(error = "ID de vehículo no encontrado") }
        }
    }

    fun onEvent(event: CotizacionVehiculoEvent) {
        when (event) {
            CotizacionVehiculoEvent.OnContinuarPagoClick -> {
            }
            CotizacionVehiculoEvent.OnVolverClick -> {
                eliminarVehiculoActual()
            }
        }
    }

    private fun eliminarVehiculoActual() {
        viewModelScope.launch {
            vehiculoId?.let { id ->
                try {
                    eliminar(id)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun cargarDatosCotizacion(id: String) {
        viewModelScope.launch {

            _state.update { it.copy(isLoading = true) }

            val result = getVehiculoUseCase(id)

            when (result) {
                is Resource.Success -> {
                    val vehiculo = result.data
                    if (vehiculo != null) {
                        val desglose = calcularPrimaUseCase(
                            valorMercado = vehiculo.valorMercado,
                            tipoCobertura = vehiculo.coverageType
                        )

                        _state.update {
                            it.copy(
                                isLoading = false,
                                vehiculoDescripcion = "${vehiculo.marca} ${vehiculo.modelo} ${vehiculo.anio}",
                                cobertura = vehiculo.coverageType,
                                valorMercado = vehiculo.valorMercado,
                                primaNeta = desglose.primaNeta,
                                impuestos = desglose.impuestos,
                                totalPagar = desglose.total
                            )
                        }
                    } else {
                        _state.update { it.copy(isLoading = false, error = "Vehículo no encontrado") }
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(isLoading = false, error = result.message ?: "Error al cargar cotización")
                    }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}