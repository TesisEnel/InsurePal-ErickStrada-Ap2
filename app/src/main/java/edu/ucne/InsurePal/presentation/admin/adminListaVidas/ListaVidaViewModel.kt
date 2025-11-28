package edu.ucne.InsurePal.presentation.admin.adminListaVidas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.polizas.vida.model.SeguroVida
import edu.ucne.InsurePal.domain.polizas.vida.useCases.GetAllSegurosVidaUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class ListaVidaViewModel @Inject constructor(
    private val getAllSegurosVidaUseCase: GetAllSegurosVidaUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LifeListUiState())
    val state: StateFlow<LifeListUiState> = _state.asStateFlow()

    init {
        loadPolicies()
    }

    fun onEvent(event: ListaVidaEvent) {
        when (event) {
            is ListaVidaEvent.OnSearchQueryChange -> {
                _state.update {
                    it.copy(
                        searchQuery = event.query,
                        filteredPolicies = filterList(it.policies, event.query)
                    )
                }
            }
            is ListaVidaEvent.OnSelectPolicy -> {
                _state.update { it.copy(selectedPolicy = event.policy, isDetailVisible = true) }
            }
            ListaVidaEvent.OnDismissDetail -> {
                _state.update { it.copy(selectedPolicy = null, isDetailVisible = false) }
            }
            ListaVidaEvent.Refresh -> {
                loadPolicies()
            }
        }
    }

    private fun loadPolicies() {
        viewModelScope.launch {
            getAllSegurosVidaUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Resource.Success -> {
                        val list = result.data ?: emptyList()
                        _state.update {
                            it.copy(
                                isLoading = false,
                                policies = list,
                                filteredPolicies = filterList(list, it.searchQuery)
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                    }
                }
            }
        }
    }

    private fun filterList(list: List<SeguroVida>, query: String): List<SeguroVida> {
        if (query.isBlank()) return list
        return list.filter {
            it.nombresAsegurado.contains(query, ignoreCase = true) ||
                    it.nombresAsegurado.contains(query, ignoreCase = true)
        }
    }
}