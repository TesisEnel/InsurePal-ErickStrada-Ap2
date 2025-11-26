package edu.ucne.InsurePal.presentation.pago

sealed interface PagoEvent {
    data class OnNumeroChange(val numero: String): PagoEvent
    data class OnFechaChange(val fecha: String): PagoEvent
    data class OnCvvChange(val cvv: String): PagoEvent
    data class OnTitularChange(val nombre: String): PagoEvent

    data object OnPagarClick: PagoEvent
    data object OnDialogDismiss: PagoEvent
}