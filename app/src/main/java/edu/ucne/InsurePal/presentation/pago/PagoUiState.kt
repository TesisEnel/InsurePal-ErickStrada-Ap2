package edu.ucne.InsurePal.presentation.pago


data class PagoUiState(
    val polizaId: String = "",
    val montoAPagar: Double = 0.0,

    val numeroTarjeta: String = "",
    val fechaVencimiento: String = "",
    val cvv: String = "",
    val titular: String = "",

    val errorNumero: String? = null,
    val errorFecha: String? = null,
    val errorCvv: String? = null,
    val errorTitular: String? = null,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val mensajeError: String? = null
)