package edu.ucne.InsurePal.data.remote.reclamoVehiculo

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.toDomain
import edu.ucne.InsurePal.domain.reclamoVehiculo.model.ReclamoVehiculo
import edu.ucne.InsurePal.domain.reclamoVehiculo.repository.ReclamoVehiculoRepository
import java.io.File
import javax.inject.Inject

class ReclamoVehiculoRepositoryImpl @Inject constructor(
    private val remoteDataSource: ReclamoRemoteDataSource
) : ReclamoVehiculoRepository {

    override suspend fun crearReclamoVehiculo(
        polizaId: String,
        usuarioId: Int,
        descripcion: String,
        direccion: String,
        tipoIncidente: String,
        fechaIncidente: String,
        numCuenta: String,
        imagen: File
    ): Resource<ReclamoVehiculo> {

        val request = ReclamoCreateRequest(
            polizaId = polizaId,
            usuarioId = usuarioId,
            descripcion = descripcion,
            direccion = direccion,
            tipoIncidente = tipoIncidente,
            numCuenta = numCuenta,
            fechaIncidente = fechaIncidente
        )

        val result = remoteDataSource.crearReclamo(request, imagen)

        return when (result) {
            is Resource.Success -> {
                Resource.Success(result.data!!.toDomain())
            }
            is Resource.Error -> {
                Resource.Error(result.message ?: "Error desconocido al crear reclamo")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun cambiarEstadoReclamoVehiculo(
        reclamoId: String,
        nuevoEstado: String,
        motivoRechazo: String?
    ): Resource<ReclamoVehiculo> {

        val request = ReclamoUpdateRequest(
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

    override suspend fun obtenerReclamoVehiculos(usuarioId: Int?): Resource<List<ReclamoVehiculo>> {
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

    override suspend fun obtenerReclamoVehiculoPorId(id: String): Resource<ReclamoVehiculo> {
        val result = remoteDataSource.getReclamo(id)

        return when (result) {
            is Resource.Success -> Resource.Success(result.data!!.toDomain())
            is Resource.Error -> Resource.Error(result.message ?: "Error al obtener el reclamo")
            is Resource.Loading -> Resource.Loading()
        }
    }
}