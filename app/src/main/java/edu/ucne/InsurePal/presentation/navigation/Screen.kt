package edu.ucne.InsurePal.presentation.navigation
import kotlinx.serialization.Serializable


sealed interface Screen {

    @Serializable
    data object Login : Screen

    @Serializable
    data object Admin : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data object SeguroVida : Screen

    @Serializable
    data object SeleccionSeguro : Screen

    @Serializable
    data class DetallePoliza(
        val policyId: String,
        val policyType: String
    ) : Screen

    @Serializable
    data object VehiculoRegistro : Screen

    @Serializable
    data object HistorialPagos : Screen

    @Serializable
    data class CotizacionDetalle(
        val vehiculoId: String
    ) : Screen

    @Serializable
    data class Pago(
        val polizaId: String,
        val monto: Double,
        val descripcion: String
    ) : Screen
}