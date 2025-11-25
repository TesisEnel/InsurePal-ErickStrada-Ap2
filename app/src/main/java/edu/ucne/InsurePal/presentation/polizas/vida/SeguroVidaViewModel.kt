package edu.ucne.InsurePal.presentation.polizas.vida


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.local.UserPreferences
import edu.ucne.InsurePal.domain.polizas.vida.model.SeguroVida
import edu.ucne.InsurePal.domain.polizas.vida.useCases.CalcularPrimaVidaUseCase
import edu.ucne.InsurePal.domain.polizas.vida.useCases.SaveSeguroVidaUseCase


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeguroVidaViewModel @Inject constructor(
    private val calcularPrima: CalcularPrimaVidaUseCase,
    private val saveSeguroVida: SaveSeguroVidaUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(SeguroVidaUiState())
    val state = _state.asStateFlow()

    private var currentUserId: Int = 0

    init {
        viewModelScope.launch {
            userPreferences.userId.collectLatest { id ->
                currentUserId = id ?: 0
            }
        }
    }

    fun onEvent(event: SeguroVidaEvent) {
        when(event) {
            is SeguroVidaEvent.OnFechaNacimientoChanged -> {
                _state.update { it.copy(fechaNacimiento = event.fecha, errorFechaNacimiento = null) }
                recalcularPrima()
            }
            is SeguroVidaEvent.OnOcupacionChanged -> {
                _state.update { it.copy(ocupacion = event.ocupacion, errorOcupacion = null) }
                recalcularPrima()
            }
            is SeguroVidaEvent.OnFumadorChanged -> {
                _state.update { it.copy(esFumador = event.esFumador) }
                recalcularPrima()
            }
            is SeguroVidaEvent.OnMontoCoberturaChanged -> {

                val limpio = event.monto.filter { it.isDigit() || it == '.' }
                _state.update { it.copy(montoCobertura = limpio, errorMontoCobertura = null) }
                recalcularPrima()
            }

            is SeguroVidaEvent.OnNombresChanged -> {
                _state.update { it.copy(nombres = event.nombres, errorNombres = null) }
            }
            is SeguroVidaEvent.OnCedulaChanged -> {
                if(event.cedula.length <= 11 && event.cedula.all { it.isDigit() }) {
                    _state.update { it.copy(cedula = event.cedula, errorCedula = null) }
                }
            }
            is SeguroVidaEvent.OnNombreBeneficiarioChanged -> {
                _state.update { it.copy(nombreBeneficiario = event.nombre, errorNombreBeneficiario = null) }
            }
            is SeguroVidaEvent.OnCedulaBeneficiarioChanged -> {
                if(event.cedula.length <= 11 && event.cedula.all { it.isDigit() }) {
                    _state.update { it.copy(cedulaBeneficiario = event.cedula, errorCedulaBeneficiario = null) }
                }
            }
            is SeguroVidaEvent.OnParentescoChanged -> {
                _state.update { it.copy(parentesco = event.parentesco, errorParentesco = null) }
            }

            SeguroVidaEvent.OnCotizarClick -> {
                cotizarSeguro()
            }
            SeguroVidaEvent.OnErrorDismiss -> {
                _state.update { it.copy(errorGlobal = null) }
            }
            SeguroVidaEvent.OnNavegacionFinalizada -> {
                // Importante: Reseteamos el flag de éxito para evitar rebotes al volver atrás
                _state.update { it.copy(isSuccess = false, cotizacionIdCreada = null) }
            }
        }
    }


    private fun recalcularPrima() {
        val uiState = _state.value

        val monto = uiState.montoCobertura.toDoubleOrNull() ?: 0.0

        if (monto > 0 && uiState.fechaNacimiento.length >= 4) {
            val primaCalculada = calcularPrima(
                fechaNacimiento = uiState.fechaNacimiento,
                esFumador = uiState.esFumador,
                ocupacion = uiState.ocupacion,
                montoCobertura = monto
            )

            _state.update { it.copy(primaCalculada = primaCalculada) }
        } else {
            _state.update { it.copy(primaCalculada = 0.0) }
        }
    }

    private fun cotizarSeguro() {
        if (!validarFormulario()) return
        if (currentUserId == 0) {
            _state.update { it.copy(errorGlobal = "Error de sesión. Usuario no identificado.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val uiState = _state.value

            val nuevoSeguro = SeguroVida(
                id = "",
                usuarioId = currentUserId,
                nombresAsegurado = uiState.nombres,
                cedulaAsegurado = uiState.cedula,
                fechaNacimiento = uiState.fechaNacimiento,
                ocupacion = uiState.ocupacion,
                esFumador = uiState.esFumador,
                nombreBeneficiario = uiState.nombreBeneficiario,
                cedulaBeneficiario = uiState.cedulaBeneficiario,
                parentesco = uiState.parentesco,
                montoCobertura = uiState.montoCobertura.toDoubleOrNull() ?: 0.0,
                prima = uiState.primaCalculada,
                esPagado = false
            )

            val result = saveSeguroVida(nuevoSeguro)

            when(result) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            cotizacionIdCreada = result.data?.id
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorGlobal = result.message ?: "Error al guardar cotización"
                        )
                    }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun validarFormulario(): Boolean {
        val s = _state.value
        var esValido = true

        val errorNombres = if(s.nombres.isBlank()) "Requerido" else null
        val errorCedula = if(s.cedula.length != 11) "Debe tener 11 dígitos" else null
        val errorFecha = if(s.fechaNacimiento.isBlank()) "Requerida" else null
        val errorOcupacion = if(s.ocupacion.isBlank()) "Seleccione una" else null
        val errorMonto = if((s.montoCobertura.toDoubleOrNull() ?: 0.0) <= 0) "Monto inválido" else null

        val errorNomBen = if(s.nombreBeneficiario.isBlank()) "Requerido" else null
        val errorCedBen = if(s.cedulaBeneficiario.length != 11) "Debe tener 11 dígitos" else null
        val errorParent = if(s.parentesco.isBlank()) "Requerido" else null

        if (errorNombres != null || errorCedula != null || errorFecha != null ||
            errorOcupacion != null || errorMonto != null || errorNomBen != null ||
            errorCedBen != null || errorParent != null) {

            esValido = false
            _state.update { it.copy(
                errorNombres = errorNombres,
                errorCedula = errorCedula,
                errorFechaNacimiento = errorFecha,
                errorOcupacion = errorOcupacion,
                errorMontoCobertura = errorMonto,
                errorNombreBeneficiario = errorNomBen,
                errorCedulaBeneficiario = errorCedBen,
                errorParentesco = errorParent
            )}
        }

        return esValido
    }
}