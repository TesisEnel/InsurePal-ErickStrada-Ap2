package edu.ucne.InsurePal.data.repositoryTest


import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.remote.reclamoVida.ReclamoVidaRemoteDataSource
import edu.ucne.InsurePal.data.remote.reclamoVida.ReclamoVidaRepositoryImpl
import edu.ucne.InsurePal.data.remote.reclamoVida.dto.ReclamoVidaCreateRequest
import edu.ucne.InsurePal.data.remote.reclamoVida.dto.ReclamoVidaResponse
import edu.ucne.InsurePal.data.remote.reclamoVida.dto.ReclamoVidaUpdateRequest
import edu.ucne.InsurePal.domain.reclamoVida.model.CrearReclamoVidaParams
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class ReclamoVidaRepositoryImplTest {


    private val remoteDataSource: ReclamoVidaRemoteDataSource = mockk()


    private lateinit var repository: ReclamoVidaRepositoryImpl

    @Before
    fun setUp() {
        repository = ReclamoVidaRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `crearReclamoVida envia el request correcto y retorna Success`() = runTest {
        val fileMock = mockk<File>()
        every { fileMock.name } returns "acta_defuncion.pdf"

        val fakeResponse = ReclamoVidaResponse(
            id = "REC-001",
            folio = "F-001",
            polizaId = "POL-123",
            usuarioId = 10,
            nombreAsegurado = "Juan Perez",
            descripcion = "Causa natural",
            lugarFallecimiento = "Clinica X",
            causaMuerte = "Infarto",
            fechaFallecimiento = "2023-11-01",
            numCuenta = "123456789",
            actaDefuncionUrl = "http://url.com/acta.pdf",
            identificacionUrl = null,
            status = "PENDIENTE",
            motivoRechazo = null,
            fechaCreacion = "2023-11-02"
        )


        coEvery {
            remoteDataSource.crearReclamoVida(any(), any(), any())
        } returns Resource.Success(fakeResponse)

        val requestSlot = slot<ReclamoVidaCreateRequest>()
        val params = CrearReclamoVidaParams(
            polizaId = "POL-123",
            usuarioId = 10,
            nombreAsegurado = "Juan Perez",
            descripcion = "Causa natural",
            lugarFallecimiento = "Clinica X",
            causaMuerte = "Infarto",
            fechaFallecimiento = "2023-11-01",
            numCuenta = "123456789",
            actaDefuncion = fileMock,
        )
        val result = repository.crearReclamoVida(params)

        coVerify {
            remoteDataSource.crearReclamoVida(capture(requestSlot), fileMock, null)
        }

        val capturedRequest = requestSlot.captured

        assertEquals("POL-123", capturedRequest.polizaId)
        assertEquals("Infarto", capturedRequest.causaMuerte)
        assertEquals(10, capturedRequest.usuarioId)

        assertTrue(result is Resource.Success)
        assertEquals("REC-001", result.data?.id)
        assertEquals("PENDIENTE", result.data?.status)
    }

    @Test
    fun `cambiarEstadoReclamoVida envia update request y retorna Success`() = runTest {
        val fakeResponse = ReclamoVidaResponse(
            id = "REC-001",
            folio = "F-001",
            polizaId = "POL-123",
            usuarioId = 10,
            nombreAsegurado = "Juan",
            descripcion = "Desc",
            lugarFallecimiento = "Lugar",
            causaMuerte = "Causa",
            fechaFallecimiento = "Fecha",
            numCuenta = "123",
            actaDefuncionUrl = null,
            identificacionUrl = null,
            status = "APROBADO",
            motivoRechazo = null,
            fechaCreacion = "Fecha"
        )

        coEvery {
            remoteDataSource.updateEstado(any(), any())
        } returns Resource.Success(fakeResponse)

        val slotUpdate = slot<ReclamoVidaUpdateRequest>()

        val result = repository.cambiarEstadoReclamoVida(
            reclamoId = "REC-001",
            nuevoEstado = "APROBADO",
            motivoRechazo = null
        )

        coVerify { remoteDataSource.updateEstado(eq("REC-001"), capture(slotUpdate)) }

        assertEquals("APROBADO", slotUpdate.captured.status)
        assertTrue(result is Resource.Success)
        assertEquals("APROBADO", result.data?.status)
    }

    @Test
    fun `obtenerReclamosVida retorna Error cuando falla el remoto`() = runTest {
        val mensajeError = "Error de servidor 500"
        coEvery { remoteDataSource.getReclamos(any()) } returns Resource.Error(mensajeError)


        val result = repository.obtenerReclamosVida(1)

        assertTrue(result is Resource.Error)
        assertEquals(mensajeError, result.message)
    }
}