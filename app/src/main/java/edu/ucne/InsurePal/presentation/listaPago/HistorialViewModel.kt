package edu.ucne.InsurePal.presentation.listaPago

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.local.UserPreferences
import edu.ucne.InsurePal.domain.pago.useCase.GetHistorialPagosUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HistorialViewModel @Inject constructor(
    private val getHistorial: GetHistorialPagosUseCase,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(HistorialPagoUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val id = userPreferences.userId.first { it != null && it > 0 }

                _state.update { it.copy(usuarioId = id?:0) }
                obtenerHistorial(id?:0)

            } catch (e: Exception) {
                _state.update { it.copy(error = "No se pudo obtener el ID de usuario.") }
            }
        }
    }

    private fun obtenerHistorial(userId: Int) {
        viewModelScope.launch {
            getHistorial(userId)
                .catch { e ->
                    // El error aquí es si la conexión con la base de datos local (DAO) falla.
                    _state.update { it.copy(error = e.message ?: "Error desconocido al cargar pagos locales") }
                }
                .collect { listaPagos ->
                    _state.update { it.copy(pagos = listaPagos) }
                }
        }
    }
}