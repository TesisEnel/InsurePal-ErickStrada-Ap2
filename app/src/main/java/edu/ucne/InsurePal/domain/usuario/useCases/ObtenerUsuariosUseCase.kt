package edu.ucne.InsurePal.domain.usuario.useCases

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.usuario.model.Usuario
import edu.ucne.InsurePal.domain.usuario.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerUsuariosUseCase @Inject constructor(
    private val repository: UsuarioRepository
) {
     operator fun invoke(): Flow<Resource<List<Usuario>>> = repository.getUsuarios()
}