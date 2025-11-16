package edu.ucne.InsurePal.presentation

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
}