package edu.ucne.InsurePal.data.repositoryTest

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.local.UserPreferences
import edu.ucne.InsurePal.data.local.pago.PagoDao
import edu.ucne.InsurePal.data.local.pago.PagoEntity
import edu.ucne.InsurePal.data.local.pago.PagoRepositoryImpl
import edu.ucne.InsurePal.data.remote.pago.PagoRemoteDataSource
import edu.ucne.InsurePal.data.remote.pago.dto.HistorialPagoDto
import edu.ucne.InsurePal.domain.pago.model.TarjetaCredito
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PagoRepositoryImplTest {

    private val remoteDataSource: PagoRemoteDataSource = mockk()
    private val pagoDao: PagoDao = mockk(relaxed = true)
    private val userPreferences: UserPreferences = mockk()

    private lateinit var repository: PagoRepositoryImpl

    @Before
    fun setUp() {
        repository = PagoRepositoryImpl(remoteDataSource, pagoDao, userPreferences)
    }

    @Test
    fun `getHistorialPagos retorna flujo de dominio correctamente`() = runTest {
        val entityList = listOf(
            PagoEntity(1, "POL-1", 1, "2023-01-01", 100.0, "**** 1234", "APROBADO", "CONF-1"),
            PagoEntity(2, "POL-2", 1, "2023-01-02", 200.0, "**** 5678", "RECHAZADO", "CONF-2")
        )

        every { pagoDao.getPagosPorUsuario(1) } returns flowOf(entityList)

        val result = repository.getHistorialPagos(1).toList()

        assertEquals(1, result.size)
        val listaPagos = result[0]
        assertEquals(2, listaPagos.size)
        assertEquals("POL-1", listaPagos[0].polizaId)
        assertEquals(100.0, listaPagos[0].monto, 0.0)
    }

    @Test
    fun `procesarPago retorna Error y no guarda en DB cuando API falla`() = runTest {
        val tarjeta = TarjetaCredito("1111222233334444", "123", "12/25", "Juan")
        val errorMsg = "Fondos insuficientes"

        every { userPreferences.userId } returns flowOf(1)
        coEvery { remoteDataSource.procesarPago(any()) } returns Resource.Error(errorMsg)

        val result = repository.procesarPago("POL-1", 100.0, tarjeta)

        assertTrue(result is Resource.Error)
        assertEquals(errorMsg, result.message)

        coVerify(exactly = 0) { pagoDao.insertPago(any()) }
    }

    @Test
    fun `sincronizarPagos inserta entidades en DAO cuando API retorna lista`() = runTest {
        val remoteList = listOf(
            HistorialPagoDto(1, "P1", 50.0, "2023-01-01", "OK", "**** 1111", "C1"),
            HistorialPagoDto(2, "P2", 60.0, "2023-01-02", "OK", "**** 2222", "C2")
        )

        coEvery { remoteDataSource.getHistorialRemoto(5) } returns Resource.Success(remoteList)

        repository.sincronizarPagos(5)

        val slotList = slot<List<PagoEntity>>()
        coVerify { pagoDao.insertAll(capture(slotList)) }

        assertEquals(2, slotList.captured.size)
        assertEquals("P1", slotList.captured[0].polizaId)
        assertEquals(5, slotList.captured[0].usuarioId)
        assertEquals("**** 1111", slotList.captured[0].tarjetaMascara)
    }

    @Test
    fun `sincronizarPagos no hace nada si API falla`() = runTest {
        coEvery { remoteDataSource.getHistorialRemoto(any()) } returns Resource.Error("Error")

        repository.sincronizarPagos(1)

        coVerify(exactly = 0) { pagoDao.insertAll(any()) }
    }
}