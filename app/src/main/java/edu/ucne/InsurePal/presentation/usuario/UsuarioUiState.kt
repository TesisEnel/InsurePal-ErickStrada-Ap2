package edu.ucne.InsurePal.presentation.usuario

import edu.ucne.InsurePal.domain.usuario.model.Usuario

data class UsuarioUiState (
    val isLoading: Boolean = false,
    val usuarios: List<Usuario> = emptyList(),
    val userMessage: String? = null,
    val usuarioId : Int? = null,
    val userName: String = "",
    val password:String? = "",

    val isDialogVisible: Boolean = false,
    val regUserName: String = "",
    val regPassword: String = "",
    val regConfirmPassword: String = ""
)
