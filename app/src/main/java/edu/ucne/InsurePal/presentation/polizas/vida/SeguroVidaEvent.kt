package edu.ucne.InsurePal.presentation.polizas.vida

sealed interface SeguroVidaEvent {

    data class OnNombresChanged(val nombres: String): SeguroVidaEvent
    data class OnCedulaChanged(val cedula: String): SeguroVidaEvent
    data class OnFechaNacimientoChanged(val fecha: String): SeguroVidaEvent
    data class OnOcupacionChanged(val ocupacion: String): SeguroVidaEvent
    data class OnFumadorChanged(val esFumador: Boolean): SeguroVidaEvent

    data class OnNombreBeneficiarioChanged(val nombre: String): SeguroVidaEvent
    data class OnCedulaBeneficiarioChanged(val cedula: String): SeguroVidaEvent
    data class OnParentescoChanged(val parentesco: String): SeguroVidaEvent

    data class OnMontoCoberturaChanged(val monto: String): SeguroVidaEvent

    data object OnCotizarClick: SeguroVidaEvent
    data object OnErrorDismiss: SeguroVidaEvent
    data object OnNavegacionFinalizada: SeguroVidaEvent
}