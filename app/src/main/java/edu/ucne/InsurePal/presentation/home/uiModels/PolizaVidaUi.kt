package edu.ucne.InsurePal.presentation.home.uiModels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.Color

data class LifePolicyUi(
    override val id: String,
    override val status: String,
    val insuredName: String,
    val coverageAmount: Double
) : PolicyUiModel {
    override val title = "Vida"
    override val icon = Icons.Default.Favorite
    override val color = Color(0xFFE91E63)
}