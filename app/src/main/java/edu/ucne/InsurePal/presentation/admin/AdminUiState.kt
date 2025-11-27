package edu.ucne.InsurePal.presentation.admin

import edu.ucne.InsurePal.presentation.home.uiModels.PolicyUiModel

data class AdminUiState (
    val isLoading: Boolean = false,
    val policies: List<PolicyUiModel> = emptyList(),
    val errorMessage: String? = null,
    val selectedPolicy: PolicyUiModel? = null,
    val isDetailVisible: Boolean = false
)