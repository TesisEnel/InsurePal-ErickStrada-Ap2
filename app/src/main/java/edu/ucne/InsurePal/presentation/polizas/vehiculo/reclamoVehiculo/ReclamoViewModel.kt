package edu.ucne.InsurePal.presentation.polizas.vehiculo.reclamoVehiculo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
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
                _uiState.update { it.copy(descripcion = event.descripcion) }
                validarFormulario()
            }
            is ReclamoEvent.DireccionChanged -> {
                _uiState.update { it.copy(direccion = event.direccion) }
                validarFormulario()
            }
            is ReclamoEvent.TipoIncidenteChanged -> {
                _uiState.update { it.copy(tipoIncidente = event.tipo) }
                validarFormulario()
            }
            is ReclamoEvent.FechaIncidenteChanged -> {
                _uiState.update { it.copy(fechaIncidente = event.fecha) }
            }
            is ReclamoEvent.FotoSeleccionada -> {
                _uiState.update { it.copy(fotoEvidencia = event.archivo) }
                validarFormulario()
            }
            is ReclamoEvent.GuardarReclamo -> {
                enviarReclamo(event.polizaId, event.usuarioId)
            }
            is ReclamoEvent.NumCuentaChanged ->{
                _uiState.update { it.copy(numCuenta = event.numCuenta) }
            }
            is ReclamoEvent.ErrorVisto -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun validarFormulario() {
        val estado = _uiState.value
        val esValido = estado.descripcion.isNotBlank() &&
                estado.direccion.isNotBlank() &&
                estado.tipoIncidente.isNotBlank() &&
                estado.fotoEvidencia != null // La foto es obligatoria

        _uiState.update { it.copy(camposValidos = esValido) }
    }

    private fun enviarReclamo(polizaId: String, usuarioId: Int) {
        val estado = _uiState.value
        if (estado.fotoEvidencia == null) {
            _uiState.update { it.copy(error = "Debes seleccionar una foto de evidencia") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val result = crearReclamoUseCase(
                    polizaId = polizaId,
                    usuarioId = usuarioId,
                    descripcion = estado.descripcion,
                    direccion = estado.direccion,
                    tipoIncidente = estado.tipoIncidente,
                    fechaIncidente = estado.fechaIncidente,
                    numCuenta = estado.numCuenta,
                    imagen = estado.fotoEvidencia
                )

                when (result) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false, esExitoso = true, error = null) }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, esExitoso = false, error = result.message) }
                    }
                    is Resource.Loading -> { }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, error = "Error: ${e.message}") }
            }
        }
    }
}