package edu.ucne.InsurePal.presentation.navigation



sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")

    object SeleccionSeguro : Screen("seleccion_seguro")

    object MisVehiculos : Screen("mis_vehiculos")

    object VehiculoRegistro : Screen("vehiculo_registro")

}