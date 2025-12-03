package edu.ucne.InsurePal.data.remote.reclamoVida

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.remote.reclamoVida.dto.ReclamoVidaCreateRequest
import edu.ucne.InsurePal.data.remote.reclamoVida.dto.ReclamoVidaUpdateRequest
import edu.ucne.InsurePal.data.toDomain
import edu.ucne.InsurePal.domain.reclamoVida.model.CrearReclamoVidaParams
import edu.ucne.InsurePal.domain.reclamoVida.model.ReclamoVida
import edu.ucne.InsurePal.domain.reclamoVida.repository.ReclamoVidaRepository
import javax.inject.Inject

class ReclamoVidaRepositoryImpl @Inject constructor(
    private val remoteDataSource: ReclamoVidaRemoteDataSource
) : ReclamoVidaRepository {

    override suspend fun crearReclamoVida(params: CrearReclamoVidaParams): Resource<ReclamoVida> {

        val requestApi = ReclamoVidaCreateRequest(
            polizaId = params.polizaId,
            usuarioId = params.usuarioId,
            nombreAsegurado = params.nombreAsegurado,
            descripcion = params.descripcion,
            lugarFallecimiento = params.lugarFallecimiento,
            causaMuerte = params.causaMuerte,
            fechaFallecimiento = params.fechaFallecimiento,
            numCuenta = params.numCuenta
        )

        val result = remoteDataSource.crearReclamoVida(
            request = requestApi,
            archivoActa = params.actaDefuncion,
            archivoIdentificacion = params.identificacion
        )

        return when (result) {
            is Resource.Success -> {
                Resource.Success(result.data?.toDomain())
            }
            is Resource.Error -> {
                Resource.Error(result.message ?: "Ha ocurrido un error desconocido")
            }
            is Resource.Loading -> {
                Resource.Loading()
            }
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