package edu.ucne.InsurePal.data.repositoryTest

import edu.ucne.InsurePal.data.remote.polizas.vehiculo.SeguroVehiculoRepositoryImpl
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.api.RemoteDataSource
import io.mockk.mockk

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.MarcaVehiculoDto
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.SeguroVehiculoRequest
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.SeguroVehiculoResponse
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.SeguroVehiculo

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.slot
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SeguroVehiculoRepositoryImplTest {

    private val remoteDataSource: RemoteDataSource = mockk()
    private lateinit var repository: SeguroVehiculoRepositoryImpl

    @Before
    fun setUp() {
        repository = SeguroVehiculoRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `getMarcas retorna Success con lista mapeada`() = runTest {
        val fakeMarcas = listOf(
            MarcaVehiculoDto(nombre = "Toyota", modelos = emptyList()),
            MarcaVehiculoDto(nombre = "Honda", modelos = emptyList())
        )

        coEvery { remoteDataSource.getMarcas() } returns Resource.Success(fakeMarcas)

        val result = repository.getMarcas()

        assertTrue(result is Resource.Success)
        assertEquals(2, result.data?.size)
        assertEquals("Toyota", result.data?.get(0)?.nombre)
    }

    @Test
    fun `getVehiculos emite Loading y luego Success`() = runTest {
        val fakeVehiculos = listOf(
            SeguroVehiculoResponse(
                idPoliza = "1", usuarioId = 1, name = "Corolla", marca = "Toyota",
                modelo = "Corolla", anio = "2020", color = "Rojo", placa = "A1",
                chasis = "C1", valorMercado = 100.0, coverageType = "Full"
            )
        )

        coEvery { remoteDataSource.getVehiculos(1) } returns Resource.Success(fakeVehiculos)

        val result = repository.getVehiculos(1).toList()

        assertEquals(2, result.size)
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result[1] is Resource.Success)
        assertEquals("Corolla", result[1].data?.get(0)?.name)
    }

    @Test
    fun `postVehiculo envia request correcto y retorna Success`() = runTest {
        val fakeResponse = SeguroVehiculoResponse(
            idPoliza = "NEW-1", usuarioId = 1, name = "Civic", marca = "Honda",
            modelo = "Civic", anio = "2022", color = "Azul", placa = "B2",
            chasis = "C2", valorMercado = 200.0, coverageType = "Basic"
        )

        val seguroInput = mockk<SeguroVehiculo>(relaxed = true)

        coEvery { remoteDataSource.save(any()) } returns Resource.Success(fakeResponse)

        val requestSlot = slot<SeguroVehiculoRequest>()

        val result = repository.postVehiculo(seguroInput)

        coVerify { remoteDataSource.save(capture(requestSlot)) }

        assertTrue(result is Resource.Success)
        assertEquals("NEW-1", result.data?.idPoliza)
    }

    @Test
    fun `putVehiculo actualiza correctamente y retorna Unit`() = runTest {
        val seguroInput = mockk<SeguroVehiculo>(relaxed = true)
        val responseDto = SeguroVehiculoResponse(
            idPoliza = "UPD-1", usuarioId = 1, name = "Upd", marca = "M",
            modelo = "Mod", anio = "2020", color = "C", placa = "P",
            chasis = "Ch", valorMercado = 10.0, coverageType = "T"
        )

        coEvery { remoteDataSource.update(any(), any()) } returns Resource.Success(Unit)

        val result = repository.putVehiculo("UPD-1", seguroInput)

        assertTrue(result is Resource.Success)
        assertEquals(Unit, result.data)
        coVerify { remoteDataSource.update(eq("UPD-1"), any()) }
    }

    @Test
    fun `getVehiculo retorna Error si el ID es vacio`() = runTest {
        val result = repository.getVehiculo("")

        assertTrue(result is Resource.Error)
        assertEquals("ID inválido", result.message)
    }

    @Test
    fun `getVehiculo retorna Success cuando encuentra el vehiculo`() = runTest {
        val fakeResponse = SeguroVehiculoResponse(
            idPoliza = "FIND-1", usuarioId = 1, name = "Found", marca = "F",
            modelo = "M", anio = "2020", color = "C", placa = "P",
            chasis = "CH", valorMercado = 100.0, coverageType = "T"
        )

        coEvery { remoteDataSource.getVehiculo("FIND-1") } returns Resource.Success(fakeResponse)

        val result = repository.getVehiculo("FIND-1")

        assertTrue(result is Resource.Success)
        assertEquals("FIND-1", result.data?.idPoliza)
    }

    @Test
    fun `delete retorna Success`() = runTest {
        coEvery { remoteDataSource.deleteVehiculo("DEL-1") } returns Resource.Success(Unit)

        val result = repository.delete("DEL-1")

        assertTrue(result is Resource.Success)
        coVerify { remoteDataSource.deleteVehiculo("DEL-1") }
    }

    @Test
    fun `getAllVehiculos retorna Error cuando falla el remoto`() = runTest {
        coEvery { remoteDataSource.getAllVehiculos() } returns Resource.Error("Fallo red")

        val result = repository.getAllVehiculos().toList()

        assertEquals(2, result.size)
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result[1] is Resource.Error)
        assertEquals("Fallo red", result[1].message)
    }
}