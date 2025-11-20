package edu.ucne.InsurePal.domain.usuario.useCases

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.toRequest
import edu.ucne.InsurePal.domain.usuario.model.Usuario
import edu.ucne.InsurePal.domain.usuario.repository.UsuarioRepository
import javax.inject.Inject

class SaveUsuarioUseCase @Inject constructor(
    private val repository: UsuarioRepository
) {
    suspend operator fun invoke(id: Int = 0, usuario: Usuario): Resource<Usuario?> {

        val usuarioRequest = usuario.toRequest()
        val result: Resource<Usuario?> = if (id == 0) {
            val postResult = repository.postUsuario(usuarioRequest)
            when (postResult) {
                is Resource.Success -> Resource.Success(postResult.data)
                is Resource.Error -> Resource.Error(postResult.message ?: "Error", postResult.data)
                is Resource.Loading -> Resource.Loading(postResult.data)
            }

        } else {
            val putResult = repository.putUsuario(id, usuarioRequest)
            when (putResult) {
                is Resource.Success -> Resource.Success(usuario)
                is Resource.Error -> Resource.Error(putResult.message ?: "Error al actualizar")
                is Resource.Loading -> Resource.Loading()
            }
        }

        return result
    }
}