package edu.ucne.InsurePal.data.remote.usuario.api

import edu.ucne.InsurePal.data.remote.usuario.dto.UsuarioRequest
import edu.ucne.InsurePal.data.remote.usuario.dto.UsuarioResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UsuarioApiService {
    @GET("api/Usuarios")
    suspend fun getUsuarios(): Response<List<UsuarioResponse>>

    @GET("api/Usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Int?): Response<UsuarioResponse>

    @POST("api/Usuarios")
    suspend fun postUsuario(@Body usuario: UsuarioRequest): Response<UsuarioResponse>

    @PUT("api/Usuarios/{id}")
    suspend fun putUsuario(@Path("id") id:Int, @Body usuario: UsuarioRequest): Response<Unit>
}