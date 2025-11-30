package edu.ucne.InsurePal.presentation.navigation
import kotlinx.serialization.Serializable


sealed interface Screen {

    @Serializable
    data object Login : Screen

    @Serializable
    data object Admin : Screen
    @Serializable
    data object ListaVida : Screen
    @Serializable
    data object ListaVehiculo : Screen
    @Serializable
    data class ReclamoVehiculo(val polizaId: String, val usuarioId: Int) : Screen
    @Serializable
    data object ReclamoVida : Screen
    @Serializable
    data object Home : Screen
    @Serializable
    data class DetalleReclamo(
        val reclamoId: String,
        val tipo: String,
        val isAdmin: Boolean = false
    ) : Screen
    @Serializable
    data object ListaReclamosVidaAdmin : Screen

    @Serializable
    data object ListaReclamosVehiculoAdmin : Screen

    @Serializable
    data object SeguroVida : Screen
    @Serializable
    data object ListaReclamos : Screen

    @Serializable
    data object SeleccionSeguro : Screen
    @Serializable
    data object SolicitudEnviada: Screen

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