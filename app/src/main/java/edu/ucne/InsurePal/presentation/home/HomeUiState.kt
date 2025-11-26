package edu.ucne.InsurePal.presentation.home

import edu.ucne.InsurePal.presentation.home.uiModels.PolicyUiModel

data class HomeUiState(
    val isLoading: Boolean = false,
    val policies: List<PolicyUiModel> = emptyList(),
    val error: String? = null,
    val userName: String = "Usuario"
)