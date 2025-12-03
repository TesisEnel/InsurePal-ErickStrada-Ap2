package edu.ucne.InsurePal.presentation.reclamoVehiculo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.reclamoVehiculo.model.CrearReclamoVehiculoParams
import edu.ucne.InsurePal.domain.reclamoVehiculo.useCases.CrearReclamoVehiculoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ReclamoViewModel @Inject constructor(
    private val crearReclamoUseCase: CrearReclamoVehiculoUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReclamoUiState())
    val uiState: StateFlow<ReclamoUiState> = _uiState.asStateFlow()

    init {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val fechaActual = LocalDateTime.now().format(formatter)
        _uiState.update { it.copy(fechaIncidente = fechaActual) }
    }

    fun onEvent(event: ReclamoEvent) {
        when (event) {
            is ReclamoEvent.DescripcionChanged -> {
                _uiState.update { it.copy(descripcion = event.descripcion, errorDescripcion = null) }
            }
            is ReclamoEvent.DireccionChanged -> {
                _uiState.update { it.copy(direccion = event.direccion, errorDireccion = null) }
            }
            is ReclamoEvent.TipoIncidenteChanged -> {
                _uiState.update { it.copy(tipoIncidente = event.tipo, errorTipoIncidente = null) }
            }
            is ReclamoEvent.FechaIncidenteChanged -> {
                _uiState.update { it.copy(fechaIncidente = event.fecha, errorFechaIncidente = null) }
            }
            is ReclamoEvent.NumCuentaChanged ->{
                _uiState.update { it.copy(numCuenta = event.numCuenta, errorNumCuenta = null) }
            }
            is ReclamoEvent.FotoSeleccionada -> {
                _uiState.update { it.copy(fotoEvidencia = event.archivo, errorFotoEvidencia = null) }
            }
            is ReclamoEvent.GuardarReclamo -> {
                enviarReclamo(event.polizaId, event.usuarioId)
            }
            is ReclamoEvent.ErrorVisto -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun validarFormulario(): Boolean {
        val estado = _uiState.value
        var esValido = true

        val errorDescripcion = if (estado.descripcion.isBlank()) {
            esValido = false; "La descripción del incidente es obligatoria"
        } else null

        val errorDireccion = if (estado.direccion.isBlank()) {
            esValido = false; "La dirección es obligatoria"
        } else null

        val errorTipo = if (estado.tipoIncidente.isBlank()) {
            esValido = false; "Debe seleccionar un tipo de incidente"
        } else null

        val errorNumCuenta = if (estado.numCuenta.isBlank()) {
            esValido = false; "El número de cuenta es requerido para depósitos"
        } else null

        val errorFoto = if (estado.fotoEvidencia == null) {
            esValido = false; "Debe adjuntar una foto de evidencia"
        } else null

        _uiState.update { it.copy(
            errorDescripcion = errorDescripcion,
            errorDireccion = errorDireccion,
            errorTipoIncidente = errorTipo,
            errorNumCuenta = errorNumCuenta,
            errorFotoEvidencia = errorFoto,
            camposValidos = esValido
        )}

        return esValido
    }

    private fun enviarReclamo(polizaId: String, usuarioId: Int) {
        if (!validarFormulario()) {
            return
        }

        val estado = _uiState.value
        if (estado.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val params = CrearReclamoVehiculoParams(
                    polizaId = polizaId,
                    usuarioId = usuarioId,
                    descripcion = estado.descripcion,
                    direccion = estado.direccion,
                    tipoIncidente = estado.tipoIncidente,
                    fechaIncidente = estado.fechaIncidente,
                    numCuenta = estado.numCuenta,
                    imagen = estado.fotoEvidencia!!
                )
                val result = crearReclamoUseCase(params)

                when (result) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false, esExitoso = true, error = null) }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, esExitoso = false, error = result.message) }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, error = "Error inesperado: ${e.message}") }
            }
        }
    }
}