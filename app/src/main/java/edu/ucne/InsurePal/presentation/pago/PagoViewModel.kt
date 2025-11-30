package edu.ucne.InsurePal.presentation.pago

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.pago.model.TarjetaCredito
import edu.ucne.InsurePal.domain.pago.useCase.ProcesarPagoUseCase
import edu.ucne.InsurePal.domain.polizas.vehiculo.useCases.GetVehiculoUseCase
import edu.ucne.InsurePal.domain.polizas.vehiculo.useCases.UpdateSeguroUseCase
import edu.ucne.InsurePal.domain.polizas.vida.useCases.GetSeguroVidaByIdUseCase
import edu.ucne.InsurePal.domain.polizas.vida.useCases.UpdateSeguroVidaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PagoViewModel @Inject constructor(
    private val procesarPago: ProcesarPagoUseCase,
    private val getVehiculoUseCase: GetVehiculoUseCase,
    private val updateSeguroUseCase: UpdateSeguroUseCase,
    private val getSeguroVidaUseCase: GetSeguroVidaByIdUseCase,
    private val updateSeguroVidaUseCase: UpdateSeguroVidaUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(PagoUiState())
    val state = _state.asStateFlow()

    private val polizaId: String = savedStateHandle["polizaId"] ?: ""
    private val monto: Double = savedStateHandle["monto"] ?: 0.0
    private val descripcion: String = savedStateHandle["descripcion"] ?: "Pago de Póliza"

    init {
        _state.update { it.copy(polizaId = polizaId, montoAPagar = monto) }
    }

    fun onEvent(event: PagoEvent) {
        when(event) {
            is PagoEvent.OnNumeroChange -> _state.update { it.copy(numeroTarjeta = event.numero) }
            is PagoEvent.OnFechaChange -> _state.update { it.copy(fechaVencimiento = event.fecha) }
            is PagoEvent.OnCvvChange -> _state.update { it.copy(cvv = event.cvv) }
            is PagoEvent.OnTitularChange -> _state.update { it.copy(titular = event.nombre) }
            PagoEvent.OnDialogDismiss -> _state.update { it.copy(mensajeError = null) }
            PagoEvent.OnPagarClick -> realizarPagoDirecto()
        }
    }

    private fun realizarPagoDirecto() {
        val uiState = _state.value

        if (uiState.numeroTarjeta.isBlank() || uiState.cvv.isBlank()) {
            _state.update { it.copy(mensajeError = "Por favor llene todos los campos") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val tarjeta = TarjetaCredito(
                numero = uiState.numeroTarjeta,
                titular = uiState.titular,
                fechaVencimiento = uiState.fechaVencimiento,
                cvv = uiState.cvv
            )

            val resultPago = procesarPago(
                polizaId = uiState.polizaId,
                monto = uiState.montoAPagar,
                tarjeta = tarjeta
            )

            when(resultPago) {
                is Resource.Success -> {
                    activarPoliza(uiState.polizaId)
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, mensajeError = resultPago.message) }
                }
                is Resource.Loading -> _state.update { it.copy(isLoading = true) }
            }
        }
    }

    private suspend fun activarPoliza(id: String) {
        if (id.startsWith("VIDA-")) {
            activarSeguroVida(id)
        } else {
            activarSeguroVehiculo(id)
        }
    }

    private suspend fun activarSeguroVida(id: String) {
        val resultGet = getSeguroVidaUseCase(id)

        if (resultGet is Resource.Success && resultGet.data != null) {
            val vidaActual = resultGet.data

            val vidaActualizada = vidaActual.copy(
                esPagado = true,
                fechaPago = LocalDate.now().plusMonths(1).toString(),
            )

            updateSeguroVidaUseCase(id, vidaActualizada)
        }
    }

    private suspend fun activarSeguroVehiculo(id: String) {
        val resultGet = getVehiculoUseCase(id)

        if (resultGet is Resource.Success && resultGet.data != null) {
            val vehiculoActual = resultGet.data

            val vehiculoActualizado = vehiculoActual.copy(
                status = "Activo",
                esPagado = true,
                fechaPago = LocalDate.now().plusMonths(1).toString(),
                expirationDate = LocalDate.now().plusMonths(1).toString()
            )

            updateSeguroUseCase(id, vehiculoActualizado)
        }
    }
}