package edu.ucne.InsurePal.presentation.listaReclamos

import edu.ucne.InsurePal.presentation.listaReclamos.UiModels.ReclamoUiItem

data class ListaReclamosUiState(
    val isLoading: Boolean = false,
    val reclamos: List<ReclamoUiItem> = emptyList(),
    val error: String? = null
)