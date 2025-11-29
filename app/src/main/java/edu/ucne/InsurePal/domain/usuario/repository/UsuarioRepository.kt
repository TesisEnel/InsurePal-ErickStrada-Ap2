package edu.ucne.InsurePal.domain.usuario.repository

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.remote.usuario.dto.UsuarioRequest
import edu.ucne.InsurePal.domain.usuario.model.Usuario
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {
    fun getUsuarios(): Flow<Resource<List<Usuario>>>
     fun getUsuario(id: Int?): Flow<Resource<Usuario>>
    suspend fun postUsuario(req: UsuarioRequest): Resource<Usuario>
    suspend fun putUsuario(id: Int, req: UsuarioRequest): Resource<Unit>
}