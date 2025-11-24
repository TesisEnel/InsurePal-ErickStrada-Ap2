package edu.ucne.InsurePal.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.ucne.InsurePal.presentation.home.InsuranceHomeScreen
import edu.ucne.InsurePal.presentation.home.SeleccionSeguroScreen
import edu.ucne.InsurePal.presentation.polizas.vehiculo.cotizacionVehiculo.CotizacionVehiculoScreen
import edu.ucne.InsurePal.presentation.polizas.vehiculo.VehiculoRegistroScreen
import edu.ucne.InsurePal.presentation.usuario.LoginScreen

@Composable
fun InsurePalNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {

                    navController.navigate(Screen.Home.route) {
                          popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            InsuranceHomeScreen(
                onActionClick = { action ->
                    if (action == "Nuevo Seguro") {
                        navController.navigate(Screen.SeleccionSeguro.route)
                    }
                }
            )
        }

        composable(Screen.SeleccionSeguro.route) {
            SeleccionSeguroScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onInsuranceSelected = { typeId ->
                    if (typeId == "VEHICULO") {
                        navController.navigate(Screen.VehiculoRegistro.route)
                    }
                }
            )
        }
        composable(Screen.VehiculoRegistro.route) {
            VehiculoRegistroScreen(
                onNavigateBack = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToCotizacion = { vehiculoId ->
                    navController.navigate(Screen.CotizacionDetalle.passId(vehiculoId))
                }
            )
        }

        composable(
            route = Screen.CotizacionDetalle.route,
            arguments = listOf(
                navArgument("vehiculoId") {
                    type = NavType.StringType
                }
            )
        ) {
            CotizacionVehiculoScreen(
                onNavigateToPayment = {
                    // TODO: Navegar a pago
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
    }

