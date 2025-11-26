package edu.ucne.InsurePal.presentation.polizas.vehiculo.registroVehiculo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.local.UserPreferences
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.SeguroVehiculoRequest
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.SeguroVehiculo
import edu.ucne.InsurePal.domain.polizas.vehiculo.repository.SeguroVehiculoRepository
import edu.ucne.InsurePal.domain.polizas.vehiculo.useCases.CalcularValorVehiculoUseCase
import edu.ucne.InsurePal.domain.polizas.vehiculo.useCases.ObtenerMarcasUseCase
import edu.ucne.InsurePal.domain.polizas.vehiculo.useCases.SaveSeguroVehiculoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class VehiculoRegistroViewModel @Inject constructor(
    private val repository: SeguroVehiculoRepository,
    private val userPreferences: UserPreferences,
    private val guardar : SaveSeguroVehiculoUseCase,
    private val getMarcas: ObtenerMarcasUseCase,
    private val calcularValor : CalcularValorVehiculoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(VehiculoUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferences.userId.collect { id ->
                _state.update { it.copy(usuarioId = id) }
            }
        }
        cargarMarcas()
    }

    fun onEvent(event: VehiculoEvent) {
        when(event) {
            is VehiculoEvent.OnNameChanged -> _state.update { it.copy(name = event.name) }
            is VehiculoEvent.OnMarcaChanged -> {
                _state.update { currentState ->
                    val modelosFiltrados = getMarcas()
                        .find { it.nombre == event.marca }
                        ?.modelos?.map { it.nombre } ?: emptyList()

                    currentState.copy(
                        marca = event.marca,
                        modelo = "",
                        modelosDisponibles = modelosFiltrados,
                        valorMercado = ""
                    )
                }
            }
            is VehiculoEvent.OnModeloChanged -> {
                _state.update { it.copy(modelo = event.modelo) }
                calcularPrecio()
            }
            is VehiculoEvent.OnAnioChanged -> {
                _state.update { it.copy(anio = event.anio) }
                calcularPrecio()
            }
            is VehiculoEvent.OnColorChanged -> _state.update { it.copy(color = event.color) }
            is VehiculoEvent.OnPlacaChanged -> _state.update { it.copy(placa = event.placa) }
            is VehiculoEvent.OnChasisChanged -> _state.update { it.copy(chasis = event.chasis) }
            is VehiculoEvent.OnValorChanged -> _state.update { it.copy(valorMercado = event.valor) }
            is VehiculoEvent.OnCoverageChanged -> _state.update { it.copy(coverageType = event.type) }

            VehiculoEvent.OnMessageShown -> _state.update { it.copy(error = null) }
            VehiculoEvent.OnGuardarClick -> guardarVehiculo()

            VehiculoEvent.OnExitoNavegacion -> _state.update {
                it.copy(isSuccess = false, vehiculoIdCreado = null)
            }

        }
    }

    private fun guardarVehiculo() {
        viewModelScope.launch {
            val uiState = _state.value

            if (uiState.usuarioId == null) {
                _state.update { it.copy(error = "No se encontró sesión de usuario. Por favor inicie sesión nuevamente.") }
                return@launch
            }
            if (uiState.marca.isBlank() || uiState.modelo.isBlank() || uiState.placa.isBlank()) {
                _state.update { it.copy(error = "Los campos Marca, Modelo y Placa son obligatorios.") }
                return@launch
            }

            _state.update { it.copy(isLoading = true) }

            val nuevoVehiculo = SeguroVehiculo(
                idPoliza = "",
                name = uiState.name.ifBlank { "${uiState.marca} ${uiState.modelo}" },
                usuarioId = uiState.usuarioId,
                marca = uiState.marca,
                modelo = uiState.modelo,
                anio = uiState.anio,
                color = uiState.color,
                placa = uiState.placa,
                chasis = uiState.chasis,
                valorMercado = uiState.valorMercado.toDoubleOrNull() ?: 0.0,
                coverageType = uiState.coverageType,
                status = "Cotizando",
                expirationDate = LocalDate.now().toString(),
                esPagado = false,
                fechaPago = null
            )

            val result = repository.postVehiculo(nuevoVehiculo)

            when(result) {
                is Resource.Success -> {
                    val idCreado = result.data?.idPoliza ?: ""

                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            vehiculoIdCreado = idCreado
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message ?: "Error al guardar") }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun cargarMarcas() {
        val marcas = getMarcas().map { it.nombre }
        _state.update { it.copy(marcasDisponibles = marcas) }
    }

    private fun calcularPrecio() {
        val currentState = _state.value

        if (currentState.marca.isNotBlank() &&
            currentState.modelo.isNotBlank() &&
            currentState.anio.isNotBlank()) {

            val precioCalculado = calcularValor(
                marca = currentState.marca,
                modelo = currentState.modelo,
                anio = currentState.anio
            )

            if (precioCalculado > 0) {
                _state.update {
                    it.copy(valorMercado = String.format("%.2f", precioCalculado))
                }
            }
        }
    }
}