package edu.ucne.InsurePal.data.remote.usuario

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.remote.usuario.dto.UsuarioRequest
import edu.ucne.InsurePal.data.remote.usuario.dto.UsuarioResponse
import javax.inject.Inject


class RemoteDataSource @Inject constructor(
    private val api: UsuarioApiService
) {
    suspend fun save(request: UsuarioRequest): Resource<UsuarioResponse> {
        return try {
            val response = api.postUsuario(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body)
                } else {
                    Resource.Error("Respuesta vacía del servidor")
                }
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun update(id: Int, request: UsuarioRequest): Resource<Unit> {
        return try {
            val response = api.putUsuario(id, request)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getUsuario(id: Int): Resource<UsuarioResponse>{
        return try{
            val response = api.getUsuario(id)
            if(response.isSuccessful){
                response.body().let { Resource.Success(it) }
            }else {
                return Resource.Error("Error ${response.code()}: ${response.message()}")
            }
        }catch (e: Exception){
            return Resource.Error("Error: ${e.localizedMessage ?: "Ocurrió un error"}")
        }
    }

    suspend fun getUsuarios(): Resource<List<UsuarioResponse>> {
        return try {
            val response = api.getUsuarios()
            if (response.isSuccessful) {
                response.body()?.let { Resource.Success(it) }
                    ?: Resource.Error("Respuesta vacía al obtener los usuarios")
            } else {
                Resource.Error("HTTP ${response.code()} al obtener lista de usuarios: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red al obtener usuarios")
        }
    }
}