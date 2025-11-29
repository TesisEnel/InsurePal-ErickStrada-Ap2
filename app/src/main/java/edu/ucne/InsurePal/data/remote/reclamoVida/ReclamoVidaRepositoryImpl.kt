package edu.ucne.InsurePal.data.remote.reclamoVida

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.remote.reclamoVida.dto.ReclamoVidaCreateRequest
import edu.ucne.InsurePal.data.remote.reclamoVida.dto.ReclamoVidaUpdateRequest
import edu.ucne.InsurePal.data.toDomain

import edu.ucne.InsurePal.domain.reclamoVida.model.ReclamoVida
import edu.ucne.InsurePal.domain.reclamoVida.repository.ReclamoVidaRepository
import java.io.File
import javax.inject.Inject

class ReclamoVidaRepositoryImpl @Inject constructor(
    private val remoteDataSource: ReclamoVidaRemoteDataSource
) : ReclamoVidaRepository {

    override suspend fun crearReclamoVida(
        polizaId: String,
        usuarioId: Int,
        nombreAsegurado: String,
        descripcion: String,
        lugarFallecimiento: String,
        causaMuerte: String,
        fechaFallecimiento: String,
        numCuenta: String,
        actaDefuncion: File
    ): Resource<ReclamoVida> {

        val request = ReclamoVidaCreateRequest(
            polizaId = polizaId,
            usuarioId = usuarioId,
            nombreAsegurado = nombreAsegurado,
            descripcion = descripcion,
            lugarFallecimiento = lugarFallecimiento,
            causaMuerte = causaMuerte,
            fechaFallecimiento = fechaFallecimiento,
            numCuenta = numCuenta
        )

        val result = remoteDataSource.crearReclamoVida(request, actaDefuncion, null)

        return when (result) {
            is Resource.Success -> {
                Resource.Success(result.data!!.toDomain())
            }
            is Resource.Error -> {
                Resource.Error(result.message ?: "Error desconocido al crear reclamo de vida")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun cambiarEstadoReclamoVida(
        reclamoId: String,
        nuevoEstado: String,
        motivoRechazo: String?
    ): Resource<ReclamoVida> {

        val request = ReclamoVidaUpdateRequest(
            status = nuevoEstado,
            motivoRechazo = motivoRechazo
        )

        val result = remoteDataSource.updateEstado(reclamoId, request)

        return when (result) {
            is Resource.Success -> Resource.Success(result.data!!.toDomain())
            is Resource.Error -> Resource.Error(result.message ?: "Error al actualizar estado")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun obtenerReclamosVida(usuarioId: Int?): Resource<List<ReclamoVida>> {
        val result = remoteDataSource.getReclamos(usuarioId)

        return when (result) {
            is Resource.Success -> {
                val listaDomain = result.data?.toDomain() ?: emptyList()
                Resource.Success(listaDomain)
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al obtener lista")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun obtenerReclamoVidaPorId(id: String): Resource<ReclamoVida> {
        val result = remoteDataSource.getReclamo(id)

        return when (result) {
            is Resource.Success -> Resource.Success(result.data!!.toDomain())
            is Resource.Error -> Resource.Error(result.message ?: "Error al obtener el reclamo")
            is Resource.Loading -> Resource.Loading()
        }
    }
}