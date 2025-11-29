package edu.ucne.InsurePal.domain.reclamoVida.repository

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.reclamoVida.model.ReclamoVida
import java.io.File

interface ReclamoVidaRepository {
    suspend fun crearReclamoVida(
        polizaId: String,
        usuarioId: Int,
        nombreAsegurado: String,
        descripcion: String,
        lugarFallecimiento: String,
        causaMuerte: String,
        fechaFallecimiento: String,
        numCuenta: String,
        actaDefuncion: File
    ): Resource<ReclamoVida>

    suspend fun obtenerReclamosVida(
        usuarioId: Int? = null
    ): Resource<List<ReclamoVida>>

    suspend fun obtenerReclamoVidaPorId(
        id: String
    ): Resource<ReclamoVida>

    suspend fun cambiarEstadoReclamoVida(
        reclamoId: String,
        nuevoEstado: String,
        motivoRechazo: String?
    ): Resource<ReclamoVida>
}