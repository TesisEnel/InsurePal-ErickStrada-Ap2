package edu.ucne.InsurePal.presentation.reclamoVida

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.local.UserPreferences
import edu.ucne.InsurePal.domain.polizas.vida.useCases.GetSeguroVidaByIdUseCase
import edu.ucne.InsurePal.domain.reclamoVida.model.CrearReclamoVidaParams
import edu.ucne.InsurePal.domain.reclamoVida.useCases.CrearReclamoVidaUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@HiltViewModel
class ReclamoVidaViewModel @Inject constructor(
    private val crearReclamoVidaUseCase: CrearReclamoVidaUseCase,
    private val getSeguroVidaByIdUseCase: GetSeguroVidaByIdUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

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
                _uiState.update { it.copy(nombreAsegurado = event.nombre, errorNombreAsegurado = null) }
            }
            is ReclamoVidaEvent.DescripcionChanged -> {
                _uiState.update { it.copy(descripcion = event.descripcion, errorDescripcion = null) }
            }
            is ReclamoVidaEvent.LugarFallecimientoChanged -> {
                _uiState.update { it.copy(lugarFallecimiento = event.lugar, errorLugarFallecimiento = null) }
            }
            is ReclamoVidaEvent.CausaMuerteChanged -> {
                _uiState.update { it.copy(causaMuerte = event.causa, errorCausaMuerte = null) }
            }
            is ReclamoVidaEvent.FechaFallecimientoChanged -> {
                _uiState.update { it.copy(fechaFallecimiento = event.fecha, errorFechaFallecimiento = null) }
            }
            is ReclamoVidaEvent.NumCuentaChanged -> {
                _uiState.update { it.copy(numCuenta = event.cuenta, errorNumCuenta = null) }
            }
            is ReclamoVidaEvent.ActaDefuncionSeleccionada -> {
                _uiState.update { it.copy(archivoActa = event.archivo, errorArchivoActa = null) }
            }
            is ReclamoVidaEvent.IdentificacionSeleccionada -> {
                _uiState.update { it.copy(archivoIdentificacion = event.archivo, errorArchivoIdentificacion = null) }
            }
            is ReclamoVidaEvent.GuardarReclamo -> {
                enviarReclamo(event.polizaId)
            }
            ReclamoVidaEvent.ErrorVisto -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun validarFormulario(): Boolean {
        val s = _uiState.value
        var esValido = true

        val errorNombre = if (s.nombreAsegurado.isBlank()) {
            esValido = false; "El nombre del asegurado es obligatorio"
        } else null

        val errorDesc = if (s.descripcion.isBlank()) {
            esValido = false; "La descripción es obligatoria"
        } else null

        val errorLugar = if (s.lugarFallecimiento.isBlank()) {
            esValido = false; "El lugar de fallecimiento es obligatorio"
        } else null

        val errorCausa = if (s.causaMuerte.isBlank()) {
            esValido = false; "La causa de muerte es obligatoria"
        } else null

        val errorFecha = if (s.fechaFallecimiento.isBlank()) {
            esValido = false; "La fecha es obligatoria"
        } else null

        val errorCuenta = if (s.numCuenta.isBlank()) {
            esValido = false; "El número de cuenta es obligatorio"
        } else null

        val errorActa = if (s.archivoActa == null) {
            esValido = false; "Debe adjuntar el Acta de Defunción"
        } else null

        // NUEVA VALIDACIÓN: Identificación obligatoria
        val errorIdentificacion = if (s.archivoIdentificacion == null) {
            esValido = false; "Debe adjuntar la Identificación (Cédula)"
        } else null

        _uiState.update { it.copy(
            errorNombreAsegurado = errorNombre,
            errorDescripcion = errorDesc,
            errorLugarFallecimiento = errorLugar,
            errorCausaMuerte = errorCausa,
            errorFechaFallecimiento = errorFecha,
            errorNumCuenta = errorCuenta,
            errorArchivoActa = errorActa,
            errorArchivoIdentificacion = errorIdentificacion, // Actualizar estado de error
            camposValidos = esValido
        )}

        return esValido
    }

    private fun enviarReclamo(polizaId: String) {
        if (!validarFormulario()) return

        val estado = _uiState.value

        if (estado.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val userId = userPreferences.userId.first() ?: 0
                if (userId == 0) {
                    _uiState.update { it.copy(isLoading = false, error = "No se pudo identificar al usuario.") }
                    return@launch
                }

                val polizaResult = getSeguroVidaByIdUseCase(polizaId)
                if (polizaResult is Resource.Error) {
                    _uiState.update { it.copy(isLoading = false, error = "La póliza no existe o no se encuentra.") }
                    return@launch
                }

                val params = CrearReclamoVidaParams(
                    polizaId = polizaId,
                    usuarioId = userId,
                    nombreAsegurado = estado.nombreAsegurado,
                    descripcion = estado.descripcion,
                    lugarFallecimiento = estado.lugarFallecimiento,
                    causaMuerte = estado.causaMuerte,
                    fechaFallecimiento = estado.fechaFallecimiento,
                    numCuenta = estado.numCuenta,
                    actaDefuncion = estado.archivoActa!!,
                    identificacion = estado.archivoIdentificacion!!
                )

                val result = crearReclamoVidaUseCase(params)

                when (result) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false, esExitoso = true) }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, error = "Excepción: ${e.message}") }
            }
        }
    }
}