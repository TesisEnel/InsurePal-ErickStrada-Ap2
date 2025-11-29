package edu.ucne.InsurePal.presentation.usuario

import edu.ucne.InsurePal.domain.usuario.model.Usuario

sealed interface UsuarioEvent
{
    data object cargar : UsuarioEvent
    data class OnUsernameChange(val userName: String) : UsuarioEvent
    data class OnPasswordChange(val password: String) : UsuarioEvent
    data class Obtener(val id: Int?): UsuarioEvent
    object userMessageShown : UsuarioEvent

    data class Crear(val usuario: Usuario): UsuarioEvent
    data class Actualizar(val usuario: Usuario) : UsuarioEvent

    data object new: UsuarioEvent
    data object onLoginClick : UsuarioEvent

    data object registerNewUser : UsuarioEvent
    data object showRegistrationDialog : UsuarioEvent
    data object hideRegistrationDialog : UsuarioEvent
    data class OnRegUsernameChange(val userName: String) : UsuarioEvent
    data class OnRegPasswordChange(val password: String) : UsuarioEvent
    data class OnRegConfirmPasswordChange(val password: String) : UsuarioEvent
}