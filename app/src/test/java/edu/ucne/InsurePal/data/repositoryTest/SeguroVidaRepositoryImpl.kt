package edu.ucne.InsurePal.data.repositoryTest

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.remote.polizas.vida.SeguroVidaRemoteDataSource
import edu.ucne.InsurePal.data.remote.polizas.vida.SeguroVidaRepositoryImpl
import edu.ucne.InsurePal.data.remote.polizas.vida.SeguroVidaRequest
import edu.ucne.InsurePal.data.remote.polizas.vida.SeguroVidaResponse
import edu.ucne.InsurePal.domain.polizas.vida.model.SeguroVida
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SeguroVidaRepositoryImplTest {

    private val remoteDataSource: SeguroVidaRemoteDataSource = mockk()
    private lateinit var repository: SeguroVidaRepositoryImpl

    @Before
    fun setUp() {
        repository = SeguroVidaRepositoryImpl(remoteDataSource)
    }

    @Test
    fun getSegurosVidaReturnsFlowWithLoadingAndSuccess() = runTest {
        val responseList = listOf(
            createFakeResponse("1"),
            createFakeResponse("2")
        )

        coEvery { remoteDataSource.getSegurosVida(10) } returns Resource.Success(responseList)

        val result = repository.getSegurosVida(10).toList()

        assertEquals(2, result.size)
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result[1] is Resource.Success)
        assertEquals(2, (result[1] as Resource.Success).data?.size)
        assertEquals("1", result[1].data?.get(0)?.id)
    }

    @Test
    fun getAllSegurosVidaReturnsErrorWhenRemoteFails() = runTest {
        val errorMessage = "Error fatal"
        coEvery { remoteDataSource.getAllSegurosVida() } returns Resource.Error(errorMessage)

        val result = repository.getAllSegurosVida().toList()

        assertEquals(2, result.size)
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result[1] is Resource.Error)
        assertEquals(errorMessage, result[1].message)
    }

    @Test
    fun getSeguroVidaByIdReturnsSuccess() = runTest {
        val fakeResponse = createFakeResponse("99")
        coEvery { remoteDataSource.getSeguroVidaById("99") } returns Resource.Success(fakeResponse)

        val result = repository.getSeguroVidaById("99")

        assertTrue(result is Resource.Success)
        assertEquals("99", result.data?.id)
        assertEquals("Juan Perez", result.data?.nombresAsegurado)
    }

    @Test
    fun saveSeguroVidaMapsToRequestAndReturnsSuccess() = runTest {
        val domainInput = createFakeDomain("NEW")
        val fakeResponse = createFakeResponse("NEW_ID")
        val requestSlot = slot<SeguroVidaRequest>()

        coEvery {
            remoteDataSource.saveSeguroVida(any())
        } returns Resource.Success(fakeResponse)

        val result = repository.saveSeguroVida(domainInput)

        coVerify { remoteDataSource.saveSeguroVida(capture(requestSlot)) }

        assertEquals(domainInput.usuarioId, requestSlot.captured.usuarioId)
        assertEquals(domainInput.nombresAsegurado, requestSlot.captured.nombresAsegurado)
        assertEquals(domainInput.montoCobertura, requestSlot.captured.montoCobertura, 0.0)

        assertTrue(result is Resource.Success)
        assertEquals("NEW_ID", result.data?.id)
    }

    @Test
    fun updateSeguroVidaReturnsSuccessUnit() = runTest {
        val domainInput = createFakeDomain("UPD")
        val requestSlot = slot<SeguroVidaRequest>()
        val responseDto = createFakeResponse("UPD")

        coEvery {
            remoteDataSource.updateSeguroVida(any(), any())
        } returns Resource.Success(responseDto)

        val result = repository.updateSeguroVida("UPD", domainInput)

        coVerify { remoteDataSource.updateSeguroVida(eq("UPD"), capture(requestSlot)) }

        assertEquals("Juan Perez", requestSlot.captured.nombresAsegurado)
        assertTrue(result is Resource.Success)
        assertEquals(Unit, result.data)
    }

    @Test
    fun deleteReturnsResourceFromDataSource() = runTest {
        coEvery { remoteDataSource.deleteSeguroVida("DEL-1") } returns Resource.Success(Unit)

        val result = repository.delete("DEL-1")

        assertTrue(result is Resource.Success)
        coVerify { remoteDataSource.deleteSeguroVida("DEL-1") }
    }

    private fun createFakeResponse(id: String) = SeguroVidaResponse(
        id = id,
        usuarioId = 1,
        nombresAsegurado = "Juan Perez",
        cedulaAsegurado = "001",
        fechaNacimiento = "2000-01-01",
        ocupacion = "Dev",
        esFumador = false,
        nombreBeneficiario = "Maria",
        cedulaBeneficiario = "002",
        parentesco = "Madre",
        montoCobertura = 1000.0,
        prima = 50.0,
        esPagado = true,
        fechaPago = "2023-01-01"
    )

    private fun createFakeDomain(id: String) = SeguroVida(
        id = id,
        usuarioId = 1,
        nombresAsegurado = "Juan Perez",
        cedulaAsegurado = "001",
        fechaNacimiento = "2000-01-01",
        ocupacion = "Dev",
        esFumador = false,
        nombreBeneficiario = "Maria",
        cedulaBeneficiario = "002",
        parentesco = "Madre",
        montoCobertura = 1000.0,
        prima = 50.0,
        esPagado = true,
        fechaPago = "2023-01-01"
    )
}