package edu.ucne.InsurePal.presentation.polizas.vida.reclamoVida

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.reclamoVida.useCases.CrearReclamoVidaUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@HiltViewModel
class ReclamoVidaViewModel @Inject constructor(
    private val crearReclamoVidaUseCase: CrearReclamoVidaUseCase
) : ViewModel() {

    private val TAG = "ReclamoVidaVM"
    private val _uiState = MutableStateFlow(ReclamoVidaUiState())
    val uiState: StateFlow<ReclamoVidaUiState> = _uiState.asStateFlow()

    init {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val fechaActual = LocalDateTime.now().format(formatter)
        _uiState.update { it.copy(fechaFallecimiento = fechaActual) }
    }

    fun onEvent(event: ReclamoVidaEvent) {
        when(event) {
            is ReclamoVidaEvent.NombreAseguradoChanged -> {
                _uiState.update { it.copy(nombreAsegurado = event.nombre) }
                validarFormulario()
            }
            is ReclamoVidaEvent.DescripcionChanged -> {
                _uiState.update { it.copy(descripcion = event.descripcion) }
                validarFormulario()
            }
            is ReclamoVidaEvent.LugarFallecimientoChanged -> {
                _uiState.update { it.copy(lugarFallecimiento = event.lugar) }
                validarFormulario()
            }
            is ReclamoVidaEvent.CausaMuerteChanged -> {
                _uiState.update { it.copy(causaMuerte = event.causa) }
                validarFormulario()
            }
            is ReclamoVidaEvent.FechaFallecimientoChanged -> {
                _uiState.update { it.copy(fechaFallecimiento = event.fecha) }
                validarFormulario()
            }
            is ReclamoVidaEvent.NumCuentaChanged -> {
                _uiState.update { it.copy(numCuenta = event.cuenta) }
                validarFormulario()
            }
            is ReclamoVidaEvent.ActaDefuncionSeleccionada -> {
                _uiState.update { it.copy(archivoActa = event.archivo) }
                validarFormulario()
            }
            is ReclamoVidaEvent.GuardarReclamo -> {
                enviarReclamo(event.polizaId, event.usuarioId)
            }
            ReclamoVidaEvent.ErrorVisto -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun validarFormulario() {
        val s = _uiState.value
        val esValido = s.nombreAsegurado.isNotBlank() &&
                s.descripcion.isNotBlank() &&
                s.lugarFallecimiento.isNotBlank() &&
                s.causaMuerte.isNotBlank() &&
                s.fechaFallecimiento.isNotBlank() &&
                s.numCuenta.isNotBlank() &&
                s.archivoActa != null

        _uiState.update { it.copy(camposValidos = esValido) }
    }

    private fun enviarReclamo(polizaId: String, usuarioId: Int) {
        val estado = _uiState.value

        if (estado.archivoActa == null) {
            _uiState.update { it.copy(error = "Debes adjuntar el Acta de Defunción") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val result = crearReclamoVidaUseCase(
                    polizaId = polizaId,
                    usuarioId = usuarioId,
                    nombreAsegurado = estado.nombreAsegurado,
                    descripcion = estado.descripcion,
                    lugarFallecimiento = estado.lugarFallecimiento,
                    causaMuerte = estado.causaMuerte,
                    fechaFallecimiento = estado.fechaFallecimiento,
                    numCuenta = estado.numCuenta,
                    actaDefuncion = estado.archivoActa
                )

                when (result) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false, esExitoso = true) }
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "Error envio: ${result.message}")
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is Resource.Loading -> {
                        //manejado al inicio
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, error = "Excepción: ${e.message}") }
            }
        }
    }
}