package edu.ucne.InsurePal.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.ucne.InsurePal.presentation.home.InsuranceHomeScreen
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
            )
        }
    }
}