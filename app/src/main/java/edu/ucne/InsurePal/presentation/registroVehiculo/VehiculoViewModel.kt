package edu.ucne.InsurePal.presentation.registroVehiculo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.local.UserPreferences
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.MarcaVehiculo
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
    private var catalogoMarcas: List<MarcaVehiculo> = emptyList()

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
            is VehiculoEvent.OnNameChanged -> _state.update { it.copy(name = event.name, errorName = null) }
            is VehiculoEvent.OnMarcaChanged -> {
                _state.update { currentState ->
                    val modelosFiltrados = catalogoMarcas
                        .find { it.nombre == event.marca }
                        ?.modelos?.map { it.nombre } ?: emptyList()

                    currentState.copy(
                        marca = event.marca,
                        errorMarca = null,
                        modelo = "",
                        modelosDisponibles = modelosFiltrados,
                        valorMercado = ""
                    )
                }
            }
            is VehiculoEvent.OnModeloChanged -> {
                _state.update { it.copy(modelo = event.modelo, errorModelo = null) }
                calcularPrecio()
            }
            is VehiculoEvent.OnAnioChanged -> {
                _state.update { it.copy(anio = event.anio, errorAnio = null) }
                calcularPrecio()
            }
            is VehiculoEvent.OnColorChanged -> _state.update { it.copy(color = event.color, errorColor = null) }
            is VehiculoEvent.OnPlacaChanged -> _state.update { it.copy(placa = event.placa, errorPlaca = null) }
            is VehiculoEvent.OnChasisChanged -> _state.update { it.copy(chasis = event.chasis, errorChasis = null) }
            is VehiculoEvent.OnValorChanged -> _state.update { it.copy(valorMercado = event.valor, errorValorMercado = null) }
            is VehiculoEvent.OnCoverageChanged -> _state.update { it.copy(coverageType = event.type) }

            VehiculoEvent.OnMessageShown -> _state.update { it.copy(error = null) }
            VehiculoEvent.OnGuardarClick -> guardarVehiculo()

            VehiculoEvent.OnExitoNavegacion -> _state.update {
                it.copy(isSuccess = false, vehiculoIdCreado = null)
            }
        }
    }

    private fun validarFormulario(): Boolean {
        val currentState = _state.value
        var isValid = true

        val errorName = if (currentState.name.isBlank()) {
            isValid = false; "El nombre o descripción es obligatorio"
        } else null

        val errorMarca = if (currentState.marca.isBlank()) {
            isValid = false; "Debe seleccionar una marca"
        } else null

        val errorModelo = if (currentState.modelo.isBlank()) {
            isValid = false; "Debe seleccionar un modelo"
        } else null

        val errorAnio = if (currentState.anio.isBlank()) {
            isValid = false; "El año es obligatorio"
        } else null

        val errorColor = if (currentState.color.isBlank()) {
            isValid = false; "El color es obligatorio"
        } else null

        val errorPlaca = if (currentState.placa.isBlank()) {
            isValid = false; "La placa es obligatoria"
        } else null

        val errorChasis = if (currentState.chasis.isBlank()) {
            isValid = false; "El chasis es obligatorio"
        } else null

        val valorDouble = currentState.valorMercado.toDoubleOrNull()
        val errorValor = if (valorDouble == null || valorDouble <= 0) {
            isValid = false; "Monto inválido"
        } else null

        _state.update {
            it.copy(
                errorName = errorName,
                errorMarca = errorMarca,
                errorModelo = errorModelo,
                errorAnio = errorAnio,
                errorColor = errorColor,
                errorPlaca = errorPlaca,
                errorChasis = errorChasis,
                errorValorMercado = errorValor
            )
        }

        return isValid
    }

    private fun guardarVehiculo() {
        viewModelScope.launch {
            if (_state.value.usuarioId == null) {
                _state.update { it.copy(error = "No se encontró sesión de usuario.") }
                return@launch
            }

            if (!validarFormulario()) {
                return@launch
            }

            _state.update { it.copy(isLoading = true) }
            val uiState = _state.value

            val nuevoVehiculo = SeguroVehiculo(
                idPoliza = "",
                name = uiState.name,
                usuarioId = uiState.usuarioId!!,
                marca = uiState.marca,
                modelo = uiState.modelo,
                anio = uiState.anio,
                color = uiState.color,
                placa = uiState.placa,
                chasis = uiState.chasis,
                valorMercado = uiState.valorMercado.toDouble(),
                coverageType = uiState.coverageType,
                status = "Pendiente de aprobación",
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
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = getMarcas()

            when(result) {
                is Resource.Success -> {
                    catalogoMarcas = result.data ?: emptyList()
                    val nombresMarcas = catalogoMarcas.map { it.nombre }

                    _state.update {
                        it.copy(
                            marcasDisponibles = nombresMarcas,
                            isLoading = false
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            error = result.message ?: "Error al cargar marcas",
                            isLoading = false
                        )
                    }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun calcularPrecio() {
        val currentState = _state.value

        if (currentState.marca.isNotBlank() &&
            currentState.modelo.isNotBlank() &&
            currentState.anio.isNotBlank()) {

            val precioCalculado = calcularValor(
                marca = currentState.marca,
                modelo = currentState.modelo,
                anio = currentState.anio,
                catalogo = catalogoMarcas
            )

            if (precioCalculado > 0) {
                _state.update {
                    it.copy(
                        valorMercado = String.format("%.2f", precioCalculado),
                        errorValorMercado = null
                    )
                }
            }
        }
    }
}