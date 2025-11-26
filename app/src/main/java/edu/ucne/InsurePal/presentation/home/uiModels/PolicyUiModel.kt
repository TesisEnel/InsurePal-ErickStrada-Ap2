package edu.ucne.InsurePal.presentation.home.uiModels

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface PolicyUiModel {
    val id: String
    val status: String
    val title: String
    val icon: ImageVector
    val color: Color
}