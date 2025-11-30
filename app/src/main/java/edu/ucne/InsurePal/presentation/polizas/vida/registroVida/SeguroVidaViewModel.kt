package edu.ucne.InsurePal.presentation.polizas.vida.registroVida

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.local.UserPreferences
import edu.ucne.InsurePal.domain.polizas.vida.model.SeguroVida
import edu.ucne.InsurePal.domain.polizas.vida.useCases.CalcularPrimaVidaUseCase
import edu.ucne.InsurePal.domain.polizas.vida.useCases.SaveSeguroVidaUseCase
import edu.ucne.InsurePal.domain.polizas.vida.useCases.ValidateSeguroVidaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeguroVidaViewModel @Inject constructor(
    private val calcularPrimaUseCase: CalcularPrimaVidaUseCase,
    private val saveSeguroVida: SaveSeguroVidaUseCase,
    private val validateSeguroVida: ValidateSeguroVidaUseCase,
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
                _state.update {
                    val newState = it.copy(fechaNacimiento = event.fecha, errorFechaNacimiento = null)
                    newState.copy(primaCalculada = calcularPrimaInterna(newState))
                }
            }
            is SeguroVidaEvent.OnOcupacionChanged -> {
                _state.update {
                    val newState = it.copy(ocupacion = event.ocupacion, errorOcupacion = null)
                    newState.copy(primaCalculada = calcularPrimaInterna(newState))
                }
            }
            is SeguroVidaEvent.OnFumadorChanged -> {
                _state.update {
                    val newState = it.copy(esFumador = event.esFumador)
                    newState.copy(primaCalculada = calcularPrimaInterna(newState))
                }
            }
            is SeguroVidaEvent.OnMontoCoberturaChanged -> {
                updateMontoCobertura(event.monto)
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
                _state.update { it.copy(isSuccess = false, cotizacionIdCreada = null) }
            }
        }
    }

    private fun calcularPrimaInterna(currentState: SeguroVidaUiState): Double {
        val monto = currentState.montoCobertura.toDoubleOrNull() ?: 0.0

        if (monto > 0 && monto <= 1_000_000 && currentState.fechaNacimiento.length >= 8) {
            return calcularPrimaUseCase(
                fechaNacimiento = currentState.fechaNacimiento,
                esFumador = currentState.esFumador,
                ocupacion = currentState.ocupacion,
                montoCobertura = monto
            )
        }
        return 0.0
    }

    private fun cotizarSeguro() {
        val s = _state.value

        val validationResult = validateSeguroVida(
            nombres = s.nombres,
            cedula = s.cedula,
            fechaNacimiento = s.fechaNacimiento,
            ocupacion = s.ocupacion,
            montoCobertura = s.montoCobertura,
            nombreBeneficiario = s.nombreBeneficiario,
            cedulaBeneficiario = s.cedulaBeneficiario,
            parentesco = s.parentesco
        )

        if (!validationResult.esValido) {
            _state.update { it.copy(
                errorNombres = validationResult.errorNombres,
                errorCedula = validationResult.errorCedula,
                errorFechaNacimiento = validationResult.errorFechaNacimiento,
                errorOcupacion = validationResult.errorOcupacion,
                errorMontoCobertura = validationResult.errorMontoCobertura,
                errorNombreBeneficiario = validationResult.errorNombreBeneficiario,
                errorCedulaBeneficiario = validationResult.errorCedulaBeneficiario,
                errorParentesco = validationResult.errorParentesco
            )}
            return
        }

        val usuarioFinal = if (currentUserId == 0) 1 else currentUserId

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val nuevoSeguro = SeguroVida(
                id = "",
                usuarioId = usuarioFinal,
                nombresAsegurado = s.nombres,
                cedulaAsegurado = s.cedula,
                fechaNacimiento = s.fechaNacimiento,
                ocupacion = s.ocupacion,
                esFumador = s.esFumador,
                nombreBeneficiario = s.nombreBeneficiario,
                cedulaBeneficiario = s.cedulaBeneficiario,
                parentesco = s.parentesco,
                montoCobertura = s.montoCobertura.toDoubleOrNull() ?: 0.0,
                prima = s.primaCalculada,
                esPagado = false
            )

            when(val result = saveSeguroVida(nuevoSeguro)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(isLoading = false, isSuccess = true, cotizacionIdCreada = result.data?.id)
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(isLoading = false, errorGlobal = result.message ?: "Error al guardar")
                    }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }
    private fun updateMontoCobertura(montoRaw: String) {
        val limpio = montoRaw.filter { it.isDigit() || it == '.' }

        _state.update {
            val montoDouble = limpio.toDoubleOrNull() ?: 0.0
            val errorMonto = if (montoDouble > 1_000_000) "Máximo 1,000,000" else null

            val newState = it.copy(
                montoCobertura = limpio,
                errorMontoCobertura = errorMonto
            )
            val nuevaPrima = if (errorMonto == null && montoDouble > 0) {
                calcularPrimaInterna(newState)
            } else {
                0.0
            }

            newState.copy(primaCalculada = nuevaPrima)
        }
    }
}