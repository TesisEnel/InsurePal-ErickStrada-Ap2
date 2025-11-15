package edu.ucne.InsurePal.domain

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.remote.usuario.dto.UsuarioRequest
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {
    suspend fun getUsuarios(): Flow<Resource<List<Usuario>>>
    suspend fun getUsuario(id: Int?): Flow<Resource<Usuario>>
    suspend fun postUsuario(req: UsuarioRequest): Resource<Usuario>
    suspend fun putUsuario(id: Int, req: UsuarioRequest): Resource<Unit>
}