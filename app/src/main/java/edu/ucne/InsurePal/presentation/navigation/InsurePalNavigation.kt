package edu.ucne.InsurePal.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import edu.ucne.InsurePal.presentation.admin.AdminScreen
import edu.ucne.InsurePal.presentation.admin.adminListaVehiculos.VehicleListScreen
import edu.ucne.InsurePal.presentation.admin.adminListaVidas.ListaVidaScreen
import edu.ucne.InsurePal.presentation.admin.adminReclamosVehiculos.ListaReclamosAdminScreen
import edu.ucne.InsurePal.presentation.admin.adminReclamosVidas.ListaReclamosVidasAdminScreen
import edu.ucne.InsurePal.presentation.detallePoliza.DetallePolizaScreen
import edu.ucne.InsurePal.presentation.home.InsuranceHomeScreen
import edu.ucne.InsurePal.presentation.home.SeleccionSeguroScreen
import edu.ucne.InsurePal.presentation.listaReclamos.ListaReclamosScreen
import edu.ucne.InsurePal.presentation.listaReclamos.detalleReclamo.DetalleReclamoScreen
import edu.ucne.InsurePal.presentation.pago.PagoScreen
import edu.ucne.InsurePal.presentation.pago.listaPago.HistorialPagosScreen
import edu.ucne.InsurePal.presentation.polizas.vehiculo.SolicitudEnviadaScreen
import edu.ucne.InsurePal.presentation.polizas.vehiculo.cotizacionVehiculo.CotizacionVehiculoScreen
import edu.ucne.InsurePal.presentation.polizas.vehiculo.registroVehiculo.VehiculoRegistroScreen
import edu.ucne.InsurePal.presentation.polizas.vehiculo.reclamoVehiculo.ReclamoScreen
import edu.ucne.InsurePal.presentation.polizas.vida.reclamoVida.ReclamoVidaScreen
import edu.ucne.InsurePal.presentation.polizas.vida.registroVida.RegistroSeguroVidaScreen
import edu.ucne.InsurePal.presentation.usuario.LoginScreen

@Composable
fun InsurePalNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login
    ) {

        composable<Screen.Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                },
                onAdminLogin = {
                    navController.navigate(Screen.Admin) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                }
            )
        }


        composable<Screen.Home> {
            InsuranceHomeScreen(
                onActionClick = { action ->
                    when (action) {
                        "Nuevo Seguro" -> navController.navigate(Screen.SeleccionSeguro)
                        "Mis Pagos" -> navController.navigate(Screen.HistorialPagos)
                        "Mis Reclamos" -> navController.navigate(Screen.ListaReclamos)
                        "Reportar Deceso" -> navController.navigate(Screen.ReclamoVida)
                    }
                },
                onPolicyClick = { id, type ->
                    navController.navigate(Screen.DetallePoliza(policyId = id, policyType = type))
                },
                onLogout = {
                    navController.navigate(Screen.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.SeleccionSeguro> {
            SeleccionSeguroScreen(
                onNavigateBack = { navController.popBackStack() },
                onInsuranceSelected = { typeId ->
                    if (typeId == "VEHICULO") {
                        navController.navigate(Screen.VehiculoRegistro)
                    }
                    if (typeId == "VIDA") {
                        navController.navigate(Screen.SeguroVida)
                    }
                }
            )
        }

        composable<Screen.DetalleReclamo> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.DetalleReclamo>()

            DetalleReclamoScreen(
                isAdmin = args.isAdmin,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Screen.VehiculoRegistro> {
            VehiculoRegistroScreen(
                onNavigateBack = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Home) { inclusive = true }
                    }
                },
                onNavigateToCotizacion = { vehiculoId ->
                    navController.navigate(Screen.CotizacionDetalle(vehiculoId = vehiculoId))
                }
            )
        }

        composable<Screen.CotizacionDetalle> {
            CotizacionVehiculoScreen(
                onNavigateToPayment = { _, _ ->
                    navController.navigate(Screen.SolicitudEnviada) {
                        popUpTo(Screen.Home) { inclusive = false }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Screen.SolicitudEnviada> {
            SolicitudEnviadaScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Screen.Pago> {
            PagoScreen(
                onNavigateBack = { navController.popBackStack() },
                onPaymentSuccess = {
                    navController.navigate(Screen.HistorialPagos) {
                        popUpTo(Screen.Home)
                    }
                }
            )
        }

        composable<Screen.SeguroVida> {
            RegistroSeguroVidaScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToPago = { idCreado, primaCalculada, descripcion ->
                    navController.navigate(
                        Screen.Pago(
                            polizaId = idCreado,
                            monto = primaCalculada,
                            descripcion = descripcion
                        )
                    )
                }
            )
        }

        composable<Screen.Admin> {
            AdminScreen(
                onNavigateToVehicles = {
                    navController.navigate(Screen.ListaVehiculo)
                },
                onNavigateToLife = {
                    navController.navigate(Screen.ListaVida)
                },
                onLogout = {
                    navController.navigate(Screen.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToVehicleClaims = {
                    navController.navigate(Screen.ListaReclamosVehiculoAdmin)
                },
                onNavigateToLifeClaims = {
                    navController.navigate(Screen.ListaReclamosVidaAdmin)
                }
            )
        }

        composable<Screen.ReclamoVida> {
            ReclamoVidaScreen(
                navigateBack = { navController.popBackStack() },
                onReclamoSuccess = {
                    navController.navigate(Screen.SolicitudEnviada) {
                        popUpTo(Screen.Home) { inclusive = false }
                    }
                }
            )
        }

        composable<Screen.ListaReclamosVehiculoAdmin> {
            ListaReclamosAdminScreen(
                onNavigateBack = { navController.popBackStack() },
                onReclamoClick = { id ->
                    navController.navigate(Screen.DetalleReclamo(id,"VEHICULO" ,isAdmin = true))
                }
            )
        }

        composable<Screen.ListaReclamosVidaAdmin> {
            ListaReclamosVidasAdminScreen (
                onNavigateBack = { navController.popBackStack() },
                onReclamoClick = { id ->
                    navController.navigate(Screen.DetalleReclamo(id, "VIDA",isAdmin = true))
                }
            )
        }

        composable<Screen.ListaVehiculo> {
            VehicleListScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Screen.ListaVida>{
            ListaVidaScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Screen.DetallePoliza> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.DetallePoliza>()

            DetallePolizaScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPago = { monto, descripcion ->
                    navController.navigate(
                        Screen.Pago(
                            polizaId = args.policyId,
                            monto = monto,
                            descripcion = descripcion
                        )
                    )
                },
                onNavigateToReclamo = { idPoliza, idUsuario ->
                    navController.navigate(
                        Screen.ReclamoVehiculo(
                            polizaId = idPoliza,
                            usuarioId = idUsuario
                        )
                    )
                }
            )
        }

        composable<Screen.ListaReclamos> {
            ListaReclamosScreen(
                onNavigateBack = { navController.popBackStack() },
                onReclamoClick = { reclamoId,tipo ->
                    navController.navigate(Screen.DetalleReclamo(reclamoId,tipo))
                }
            )
        }

        composable<Screen.ReclamoVehiculo> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.ReclamoVehiculo>()
            ReclamoScreen(
                polizaId = args.polizaId,
                usuarioId = args.usuarioId,
                navigateBack = { navController.popBackStack() },
                onReclamoSuccess = {
                    navController.navigate(Screen.SolicitudEnviada) {
                        popUpTo(Screen.Home) { inclusive = false }
                    }
                }
            )
        }

        composable<Screen.HistorialPagos> {
            HistorialPagosScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}