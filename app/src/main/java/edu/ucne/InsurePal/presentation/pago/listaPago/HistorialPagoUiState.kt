package edu.ucne.InsurePal.presentation.pago.listaPago

import edu.ucne.InsurePal.domain.pago.model.Pago

data class HistorialPagoUiState (
    val pagos: List<Pago> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val usuarioId: Int = 0
)