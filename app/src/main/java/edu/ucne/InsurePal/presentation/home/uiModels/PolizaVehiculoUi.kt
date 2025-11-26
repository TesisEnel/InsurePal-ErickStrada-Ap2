package edu.ucne.InsurePal.presentation.home.uiModels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.ui.graphics.Color

data class VehiclePolicyUi(
    override val id: String,
    override val status: String,
    val vehicleModel: String,
    val plate: String
) : PolicyUiModel {
    override val title = "Vehículo"
    override val icon = Icons.Default.DirectionsCar
    override val color = Color(0xFF2196F3) // Azul
}