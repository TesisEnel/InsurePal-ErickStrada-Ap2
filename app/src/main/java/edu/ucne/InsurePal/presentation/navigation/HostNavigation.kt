package edu.ucne.InsurePal.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.ucne.InsurePal.presentation.usuario.LoginScreen


@Composable
fun HostNavigation(
    navHostController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Login,
        modifier = modifier
    ) {
        composable<Screen.Login> {
            LoginScreen(
            )
        }
    }
}