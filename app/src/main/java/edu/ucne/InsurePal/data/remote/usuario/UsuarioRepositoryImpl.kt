package edu.ucne.InsurePal.data.remote.usuario

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.remote.usuario.api.RemoteDataSource
import edu.ucne.InsurePal.data.remote.usuario.dto.UsuarioRequest
import edu.ucne.InsurePal.data.toDomain
import edu.ucne.InsurePal.domain.usuario.model.Usuario
import edu.ucne.InsurePal.domain.usuario.repository.UsuarioRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UsuarioRepositoryImpl @Inject constructor(
    val remoteDataSource: RemoteDataSource
): UsuarioRepository {

    override fun getUsuarios(): Flow<Resource<List<Usuario>>> = flow {
        emit(Resource.Loading())
        when (val result = remoteDataSource.getUsuarios()) {
            is Resource.Success -> {
                val list = result.data?.map { it.toDomain() } ?: emptyList()
                emit(Resource.Success(list))
            }
            is Resource.Error -> {
                emit(Resource.Error(result.message ?: "Error al obtener usuarios"))
            }
            is Resource.Loading -> {
                emit(Resource.Loading())
            }
        }
    }

    override suspend fun postUsuario(req: UsuarioRequest): Resource<Usuario> {

        return when (val result = remoteDataSource.save(req)) {
            is Resource.Success -> {
                val response = result.data
                Resource.Success(response?.toDomain())
            }
            is Resource.Error -> {
                Resource.Error(result.message ?: "Error al guardar")
            }
            else -> {
                Resource.Error("Error desconocido al guardar")
            }
        }
    }

    override suspend fun putUsuario(id: Int, req: UsuarioRequest): Resource<Unit> {
        return when(val result = remoteDataSource.update(id, req)){
            is Resource.Success -> {
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Resource.Error(result.message ?: "Error al actualizar")
            }
            else -> {
                Resource.Error("Error desconocido al actualizar")
            }
        }
    }

    override fun getUsuario(id: Int?): Flow<Resource<Usuario>> = flow {
        if (id == null) {
            emit(Resource.Error("ID de usuario no puede ser nulo"))
            return@flow
        }

        emit(Resource.Loading())
        when(val result = remoteDataSource.getUsuario(id)){
            is Resource.Success -> {
                val usuario = result.data?.toDomain()
                if (usuario != null) {
                    emit(Resource.Success(usuario))
                } else {
                    emit(Resource.Error("Usuario no encontrado o datos corruptos"))
                }
            }
            is Resource.Error -> {
                emit(Resource.Error(result.message ?: "Error al obtener usuario")) // Emitir Resource.Error
            }
            is Resource.Loading -> {
                emit(Resource.Loading())
            }
        }
    }
}