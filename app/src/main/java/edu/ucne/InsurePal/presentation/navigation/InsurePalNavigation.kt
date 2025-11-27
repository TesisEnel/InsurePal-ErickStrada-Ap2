package edu.ucne.InsurePal.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import edu.ucne.InsurePal.presentation.admin.AdminScreen
import edu.ucne.InsurePal.presentation.admin.adminListaVehiculos.VehicleListScreen
import edu.ucne.InsurePal.presentation.detallePoliza.DetallePolizaScreen
import edu.ucne.InsurePal.presentation.home.InsuranceHomeScreen
import edu.ucne.InsurePal.presentation.home.SeleccionSeguroScreen
import edu.ucne.InsurePal.presentation.pago.PagoScreen
import edu.ucne.InsurePal.presentation.pago.listaPago.HistorialPagosScreen
import edu.ucne.InsurePal.presentation.polizas.vehiculo.cotizacionVehiculo.CotizacionVehiculoScreen
import edu.ucne.InsurePal.presentation.polizas.vehiculo.VehiculoRegistroScreen
import edu.ucne.InsurePal.presentation.polizas.vida.RegistroSeguroVidaScreen
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
                    }
                },
                onPolicyClick = { id, type ->
                    navController.navigate(Screen.DetallePoliza(policyId = id, policyType = type))
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

        composable<Screen.CotizacionDetalle> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.CotizacionDetalle>()

            CotizacionVehiculoScreen(
                onNavigateToPayment = { montoRecibido, descripcionRecibida ->

                    navController.navigate(
                        Screen.Pago(
                            polizaId = args.vehiculoId,
                            monto = montoRecibido,
                            descripcion = descripcionRecibida
                        )
                    )
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Screen.Pago> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.Pago>()

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
                            polizaId = "VIDA-$idCreado",
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
                }
            )
        }

        composable<Screen.ListaVehiculo> {
            VehicleListScreen(
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