package edu.ucne.InsurePal.presentation.usuario

import edu.ucne.InsurePal.domain.Usuario

sealed interface UsuarioEvent
{
    data object cargar : UsuarioEvent
    data class onUsernameChange(val userName: String) : UsuarioEvent
    data class onPasswordChange(val password: String) : UsuarioEvent
    data class obtener(val id: Int?): UsuarioEvent
    object userMessageShown : UsuarioEvent

    data class crear(val usuario: Usuario): UsuarioEvent
    data class actualizar(val usuario: Usuario) : UsuarioEvent

    data object new: UsuarioEvent
    data object onLoginClick : UsuarioEvent

    data object registerNewUser : UsuarioEvent
    data object showRegistrationDialog : UsuarioEvent
    data object hideRegistrationDialog : UsuarioEvent
    data class onRegUsernameChange(val userName: String) : UsuarioEvent
    data class onRegPasswordChange(val password: String) : UsuarioEvent
    data class onRegConfirmPasswordChange(val password: String) : UsuarioEvent
}