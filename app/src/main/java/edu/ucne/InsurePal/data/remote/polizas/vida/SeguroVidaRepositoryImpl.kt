package edu.ucne.InsurePal.data.remote.polizas.vida

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.toDomain
import edu.ucne.InsurePal.data.toRequest
import edu.ucne.InsurePal.domain.polizas.vida.SeguroVida
import edu.ucne.InsurePal.domain.polizas.vida.SeguroVidaRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SeguroVidaRepositoryImpl @Inject constructor(
    private val remoteDataSource: SeguroVidaRemoteDataSource
) : SeguroVidaRepository {

    override fun getSegurosVida(usuarioId: Int): Flow<Resource<List<SeguroVida>>> = flow {
        emit(Resource.Loading())

        val result = remoteDataSource.getSegurosVida(usuarioId)

        when(result) {
            is Resource.Success -> {
                val seguros = result.data?.map { it.toDomain() } ?: emptyList()
                emit(Resource.Success(seguros))
            }
            is Resource.Error -> {
                emit(Resource.Error(result.message ?: "Error desconocido"))
            }
            is Resource.Loading -> emit(Resource.Loading())
        }
    }

    override suspend fun getSeguroVidaById(id: String): Resource<SeguroVida> {
        val result = remoteDataSource.getSeguroVidaById(id)

        return when(result) {
            is Resource.Success -> {
                Resource.Success(result.data!!.toDomain())
            }
            is Resource.Error -> {
                Resource.Error(result.message ?: "Error al obtener el seguro")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun saveSeguroVida(seguro: SeguroVida): Resource<SeguroVida> {
        val request = seguro.toRequest()
        val result = remoteDataSource.saveSeguroVida(request)

        return when(result) {
            is Resource.Success -> {
                Resource.Success(result.data!!.toDomain())
            }
            is Resource.Error -> {
                Resource.Error(result.message ?: "Error al guardar")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun updateSeguroVida(id: String, seguro: SeguroVida): Resource<SeguroVida> {
        val request = seguro.toRequest()
        val result = remoteDataSource.updateSeguroVida(id, request)

        return when(result) {
            is Resource.Success -> {
                Resource.Success(result.data!!.toDomain())
            }
            is Resource.Error -> {
                Resource.Error(result.message ?: "Error al actualizar")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun delete(id: String): Resource<Unit> {
        return remoteDataSource.deleteSeguroVida(id)
    }
}