package edu.ucne.InsurePal.presentation.navigation



sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
}