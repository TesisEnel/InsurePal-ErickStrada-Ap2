package edu.ucne.InsurePal.data.remote.usuario

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.remote.usuario.api.RemoteDataSource
import edu.ucne.InsurePal.data.remote.usuario.dto.UsuarioRequest
import edu.ucne.InsurePal.data.remote.usuario.dto.UsuarioResponse
import edu.ucne.InsurePal.data.toDomain
import io.mockk.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class UsuarioRepositoryImplTest {

    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var repository: UsuarioRepositoryImpl

    @Before
    fun setUp() {
        remoteDataSource = mockk()
        repository = UsuarioRepositoryImpl(remoteDataSource)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getUsuarios devuelve éxito con lista de usuarios`() = runTest {
        val usuarioResponse1 = UsuarioResponse(1, "Ana", "pass123")
        val usuarioResponse2 = UsuarioResponse(2, "Luis", "pass456")
        val dtoList = listOf(usuarioResponse1, usuarioResponse2)
        val domainList = dtoList.map { it.toDomain() }

        coEvery { remoteDataSource.getUsuarios() } returns Resource.Success(dtoList)

        val emissions = repository.getUsuarios().toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)
        assertEquals(domainList, (emissions[1] as Resource.Success).data)

        coVerify(exactly = 1) { remoteDataSource.getUsuarios() }
    }

    @Test
    fun `getUsuarios devuelve error`() = runTest {
        val errorMessage = "Error de red"
        coEvery { remoteDataSource.getUsuarios() } returns Resource.Error(errorMessage)

        val emissions = repository.getUsuarios().toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)
        assertEquals(errorMessage, (emissions[1] as Resource.Error).message)

        coVerify(exactly = 1) { remoteDataSource.getUsuarios() }
    }

    @Test
    fun `postUsuario devuelve éxito`() = runTest {
        val request = UsuarioRequest(userName = "Nuevo Usuario", password = "123")
        val response = UsuarioResponse(usuarioId = 10, userName = "Nuevo Usuario", password = "123")
        val expectedDomain = response.toDomain()

        coEvery { remoteDataSource.save(request) } returns Resource.Success(response)

        val result = repository.postUsuario(request)

        assertTrue(result is Resource.Success)
        assertEquals(expectedDomain, (result as Resource.Success).data)

        coVerify(exactly = 1) { remoteDataSource.save(request) }
    }

    @Test
    fun `postUsuario devuelve error`() = runTest {
        val request = UsuarioRequest(userName = "Nuevo Usuario", password = "123")
        val errorMessage = "No se pudo guardar"

        coEvery { remoteDataSource.save(request) } returns Resource.Error(errorMessage)

        val result = repository.postUsuario(request)

        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, (result as Resource.Error).message)

        coVerify(exactly = 1) { remoteDataSource.save(request) }
    }

    @Test
    fun `putUsuario devuelve éxito`() = runTest {
        val userId = 1
        val request = UsuarioRequest(userName = "Usuario Actualizado", password = "456")

        coEvery { remoteDataSource.update(userId, request) } returns Resource.Success(Unit)

        val result = repository.putUsuario(userId, request)

        assertTrue(result is Resource.Success)
        assertEquals(Unit, (result as Resource.Success).data)

        coVerify(exactly = 1) { remoteDataSource.update(userId, request) }
    }

    @Test
    fun `putUsuario devuelve error`() = runTest {
        val userId = 1
        val request = UsuarioRequest(userName = "Usuario Actualizado", password = "456")
        val errorMessage = "Error al actualizar"

        coEvery { remoteDataSource.update(userId, request) } returns Resource.Error(errorMessage)

        val result = repository.putUsuario(userId, request)

        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, (result as Resource.Error).message)

        coVerify(exactly = 1) { remoteDataSource.update(userId, request) }
    }

    @Test
    fun `getUsuario devuelve éxito`() = runTest {
        val userId = 5
        val response = UsuarioResponse(usuarioId = userId, userName = "Usuario 5", password = "user5pass")
        val expectedDomain = response.toDomain()

        coEvery { remoteDataSource.getUsuario(userId) } returns Resource.Success(response)

        val emissions = repository.getUsuario(userId).toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)
        assertEquals(expectedDomain, (emissions[1] as Resource.Success).data)

        coVerify(exactly = 1) { remoteDataSource.getUsuario(userId) }
    }

    @Test
    fun `getUsuario devuelve error por ID nulo`() = runTest {
        val userId = null

        val emissions = repository.getUsuario(userId).toList()

        assertEquals(1, emissions.size)
        assertTrue(emissions[0] is Resource.Error)
        assertEquals("ID de usuario no puede ser nulo", (emissions[0] as Resource.Error).message)

        coVerify(exactly = 0) { remoteDataSource.getUsuario(any()) }
    }

    @Test
    fun `getUsuario devuelve error desde data source`() = runTest {
        val userId = 99
        val errorMessage = "Usuario no encontrado"

        coEvery { remoteDataSource.getUsuario(userId) } returns Resource.Error(errorMessage)

        val emissions = repository.getUsuario(userId).toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)
        assertEquals(errorMessage, (emissions[1] as Resource.Error).message)

        coVerify(exactly = 1) { remoteDataSource.getUsuario(userId) }
    }
}