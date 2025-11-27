package edu.ucne.InsurePal.presentation.admin

sealed interface AdminEvent {
    data object LoadDashboard : AdminEvent
    data class OnSelectPolicy(val policyId: String) : AdminEvent
    data object OnDismissDetail : AdminEvent
    data object OnLogout : AdminEvent
}