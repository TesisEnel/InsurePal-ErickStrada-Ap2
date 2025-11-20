package edu.ucne.InsurePal.presentation.usuario

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.usuario.model.Usuario
import edu.ucne.InsurePal.domain.usuario.useCases.ObtenerUsuarioUseCase
import edu.ucne.InsurePal.domain.usuario.useCases.ObtenerUsuariosUseCase
import edu.ucne.InsurePal.domain.usuario.useCases.SaveUsuarioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsuarioViewModel @Inject constructor(
    private val guardar : SaveUsuarioUseCase,
    private val obtener : ObtenerUsuarioUseCase,
    private val obtenerLista : ObtenerUsuariosUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(UsuarioUiState(isLoading = true))
    val state: StateFlow<UsuarioUiState> = _state.asStateFlow()

    init {
        obtenerUsuarios()
    }
    fun obtenerUsuarios() {
        viewModelScope.launch {
            obtenerLista().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = true
                        )
                    }

                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            usuarios = resource.data ?: emptyList(),
                            isLoading = false
                        )
                    }

                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            userMessage = resource.message ?: "Error desconocido",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: UsuarioEvent) {
        when (event) {
            is UsuarioEvent.Crear -> crearUsuario(event.usuario)
            is UsuarioEvent.actualizar -> updateUsuario(event.usuario)
            is UsuarioEvent.Obtener -> getUsuario(event.id)
            is UsuarioEvent.cargar -> obtenerUsuarios()
            is UsuarioEvent.OnPasswordChange -> {
                _state.value = _state.value.copy(password = event.password)
            }
            is UsuarioEvent.OnUsernameChange -> {
                _state.value = _state.value.copy(userName = event.userName)
            }
            is UsuarioEvent.userMessageShown -> clearMessage()
            UsuarioEvent.new -> {
                clearForm()
                _state.value = _state.value.copy()
            }
            UsuarioEvent.onLoginClick -> login()

            UsuarioEvent.showRegistrationDialog -> {
                _state.update { it.copy(isDialogVisible = true) }
            }

            UsuarioEvent.hideRegistrationDialog -> {
                _state.update {
                    it.copy(
                        isDialogVisible = false,
                        regUserName = "",
                        regPassword = "",
                        regConfirmPassword = ""
                    )
                }
            }

            UsuarioEvent.registerNewUser -> {

                viewModelScope.launch {
                    val state = _state.value
                    if (state.regPassword != state.regConfirmPassword) {
                        _state.update { it.copy(userMessage = "Las contraseñas no coinciden") }
                        return@launch
                    }

                    val usuario = Usuario(
                        usuarioId = 0,
                        userName = state.regUserName,
                        password = state.regPassword
                    )

                    when (guardar(0, usuario)) {
                        is Resource.Success -> {
                            _state.update {
                                it.copy(
                                    userMessage = "Usuario registrado exitosamente",
                                    isDialogVisible = false,
                                    regUserName = "",
                                    regPassword = "",
                                    regConfirmPassword = ""
                                )
                            }

                        }
                        is Resource.Error -> {
                            _state.update { it.copy(userMessage = "Error al registrar el usuario") }
                        }
                        else -> {}
                    }
                }
            }
            is UsuarioEvent.OnRegUsernameChange -> {
                _state.update { it.copy(regUserName = event.userName) }
            }
            is UsuarioEvent.OnRegPasswordChange -> {
                _state.update { it.copy(regPassword = event.password) }
            }
            is UsuarioEvent.OnRegConfirmPasswordChange -> {
                _state.update { it.copy(regConfirmPassword = event.password) }
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, userMessage = null) }

            val userName = _state.value.userName
            val password = _state.value.password

            obtenerLista().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val user = resource.data?.find { it.userName == userName }

                        when {
                            user == null -> {
                                _state.update {
                                    it.copy(isLoading = false, userMessage = "Usuario no encontrado")
                                }
                            }
                            user.password == password -> {
                                _state.update {
                                    it.copy(isLoading = false, userMessage = "Inicio de sesión exitoso")
                                }
                            }
                            else -> {
                                _state.update {
                                    it.copy(isLoading = false, userMessage = "Contraseña incorrecta")
                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(isLoading = false, userMessage = resource.message ?: "Error desconocido")
                        }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    private fun crearUsuario(usuario: Usuario) {
        viewModelScope.launch {
            when (val result = guardar(id = 0,usuario)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            userMessage = "Usuario creado",
                        )
                    }
                    clearForm()
                    obtenerUsuarios()
                }
                is Resource.Error -> {
                    Log.e("UsuarioViewModel", "Resultado: Resource.Error - Mensaje: ${result.message}")
                    _state.update {
                        it.copy(userMessage = "Error al crear el usuario")
                    }
                }
                else -> {
                    Log.e("UsuarioViewModel", "Resultado: $result")
                }
            }
        }
    }
    private fun updateUsuario(usuario: Usuario) {
        viewModelScope.launch {
            when (guardar(usuario.usuarioId, usuario)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            userMessage = "Usuario actualizado exitosamente",
                        )
                    }
                    clearForm()
                    obtenerUsuarios()
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(userMessage = "Error al actualizar el usuario")
                    }
                }
               else -> {}
            }
        }
    }

    private fun getUsuario(id: Int?) {
        viewModelScope.launch {
            obtener(id).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { usuario ->
                            _state.value = _state.value.copy(
                                usuarioId = usuario.usuarioId,
                                userName = usuario.userName,
                                password = usuario.password
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(userMessage = resource.message ?: "Error al cargar el usuario")
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun clearForm() {
        _state.update {
            it.copy(
                usuarioId = null,
            )
        }
    }

    private fun clearMessage() {
        _state.update { it.copy(userMessage = null) }
    }
}