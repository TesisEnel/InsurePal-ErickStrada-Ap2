package edu.ucne.InsurePal.data

import edu.ucne.InsurePal.data.remote.usuario.api.RemoteDataSource
import edu.ucne.InsurePal.data.remote.usuario.api.UsuarioApiService
import edu.ucne.InsurePal.data.remote.usuario.dto.UsuarioRequest
import edu.ucne.InsurePal.data.remote.usuario.dto.UsuarioResponse
import io.mockk.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class RemoteDataSourceTest {

    private lateinit var api: UsuarioApiService

    private lateinit var dataSource: RemoteDataSource

    private val errorResponse404: Response<Nothing> = Response.error(
        404,
        "{\"error\":\"No encontrado\"}".toResponseBody(null)
    )


    private val errorResponse500: Response<Nothing> = Response.error(
        500,
        "{\"error\":\"Error interno\"}".toResponseBody(null)
    )

    @Before
    fun setUp() {
        api = mockk()
        dataSource = RemoteDataSource(api)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `save devuelve Success cuando la API es exitosa`() = runTest {
        val request = UsuarioRequest("user", "pass")
        val expectedResponse = UsuarioResponse(1, "user", "pass")
        val apiSuccessResponse = Response.success(expectedResponse)

        coEvery { api.postUsuario(request) } returns apiSuccessResponse


        val result = dataSource.save(request)

        assertTrue(result is Resource.Success)
        assertEquals(expectedResponse, result.data)
        coVerify(exactly = 1) { api.postUsuario(request) }
    }

    @Test
    fun `save devuelve Error si la API es exitosa pero el cuerpo es nulo`() = runTest {
        val request = UsuarioRequest("user", "pass")
        val emptyBodyResponse = Response.success<UsuarioResponse>(null)

        coEvery { api.postUsuario(request) } returns emptyBodyResponse

        val result = dataSource.save(request)

        assertTrue(result is Resource.Error)
        assertEquals("Respuesta vacía del servidor", result.message)
    }

    @Test
    fun `save devuelve Error cuando la API falla (isSuccessful false)`() = runTest {
        val request = UsuarioRequest("user", "pass")

        coEvery { api.postUsuario(request) } returns (errorResponse404 as Response<UsuarioResponse>)

        val result = dataSource.save(request)

        assertTrue(result is Resource.Error)
        assertTrue(result.message!!.contains("HTTP 404"))
    }

    @Test
    fun `save devuelve Error cuando la API lanza una excepción`() = runTest {
        val request = UsuarioRequest("user", "pass")
        val exceptionMessage = "Error de red"

        coEvery { api.postUsuario(request) } throws Exception(exceptionMessage)

        val result = dataSource.save(request)

        assertTrue(result is Resource.Error)
        assertEquals(exceptionMessage, result.message)
    }


    @Test
    fun `update devuelve Success(Unit) cuando la API es exitosa`() = runTest {
        val request = UsuarioRequest("user", "pass")
        val userId = 1
        val apiSuccessResponse = Response.success(Unit)

        coEvery { api.putUsuario(userId, request) } returns apiSuccessResponse

        val result = dataSource.update(userId, request)

        assertTrue(result is Resource.Success)
        assertEquals(Unit, result.data)
        coVerify(exactly = 1) { api.putUsuario(userId, request) }
    }

    @Test
    fun `update devuelve Error cuando la API falla`() = runTest {
        val request = UsuarioRequest("user", "pass")
        val userId = 1

        coEvery { api.putUsuario(userId, request) } returns (errorResponse500 as Response<Unit>)

        val result = dataSource.update(userId, request)

        assertTrue(result is Resource.Error)
        assertTrue(result.message!!.contains("HTTP 500"))
    }

    @Test
    fun `update devuelve Error cuando la API lanza una excepción`() = runTest {
        val request = UsuarioRequest("user", "pass")
        val userId = 1
        val exceptionMessage = "Sin conexión"

        coEvery { api.putUsuario(userId, request) } throws Exception(exceptionMessage)

        val result = dataSource.update(userId, request)

        assertTrue(result is Resource.Error)
        assertEquals(exceptionMessage, result.message)
    }


    @Test
    fun `getUsuario devuelve Success cuando la API es exitosa`() = runTest {
        val userId = 1
        val expectedResponse = UsuarioResponse(1, "user", "pass")
        val apiSuccessResponse = Response.success(expectedResponse)

        coEvery { api.getUsuario(userId) } returns apiSuccessResponse

        val result = dataSource.getUsuario(userId)

        assertTrue(result is Resource.Success)
        assertEquals(expectedResponse, result.data)
        coVerify(exactly = 1) { api.getUsuario(userId) }
    }

    @Test
    fun `getUsuario devuelve Success(null) si API es exitosa pero cuerpo es nulo`() = runTest {

        val userId = 1
        val emptyBodyResponse = Response.success<UsuarioResponse>(null)

        coEvery { api.getUsuario(userId) } returns emptyBodyResponse

        val result = dataSource.getUsuario(userId)

        assertTrue(result is Resource.Success)
        assertNull(result.data)
    }

    @Test
    fun `getUsuario devuelve Error cuando la API falla`() = runTest {
        val userId = 99

        coEvery { api.getUsuario(userId) } returns (errorResponse404 as Response<UsuarioResponse>)

        val result = dataSource.getUsuario(userId)

        assertTrue(result is Resource.Error)
        assertTrue(result.message!!.contains("Error 404"))
    }

    @Test
    fun `getUsuario devuelve Error cuando la API lanza una excepción`() = runTest {
        val userId = 1
        val exceptionMessage = "Timeout"

        coEvery { api.getUsuario(userId) } throws Exception(exceptionMessage)

        val result = dataSource.getUsuario(userId)

        assertTrue(result is Resource.Error)
        assertTrue(result.message!!.contains(exceptionMessage))
    }


    @Test
    fun `getUsuarios devuelve Success con lista cuando la API es exitosa`() = runTest {
        val expectedList = listOf(UsuarioResponse(1, "user1", "p1"))
        val apiSuccessResponse = Response.success(expectedList)

        coEvery { api.getUsuarios() } returns apiSuccessResponse

        val result = dataSource.getUsuarios()

        assertTrue(result is Resource.Success)
        assertEquals(expectedList, result.data)
        coVerify(exactly = 1) { api.getUsuarios() }
    }

    @Test
    fun `getUsuarios devuelve Error si la API es exitosa pero el cuerpo es nulo`() = runTest {
        val emptyBodyResponse = Response.success<List<UsuarioResponse>>(null)

        coEvery { api.getUsuarios() } returns emptyBodyResponse

        val result = dataSource.getUsuarios()

        assertTrue(result is Resource.Error)
        assertEquals("Respuesta vacía al obtener los usuarios", result.message)
    }

    @Test
    fun `getUsuarios devuelve Error cuando la API falla`() = runTest {
        coEvery { api.getUsuarios() } returns (errorResponse500 as Response<List<UsuarioResponse>>)

        val result = dataSource.getUsuarios()

        assertTrue(result is Resource.Error)
        assertTrue(result.message!!.contains("HTTP 500"))
    }

    @Test
    fun `getUsuarios devuelve Error cuando la API lanza una excepción`() = runTest {
        val exceptionMessage = "DNS no resuelto"
        coEvery { api.getUsuarios() } throws Exception(exceptionMessage)

        val result = dataSource.getUsuarios()

        assertTrue(result is Resource.Error)
        assertEquals(exceptionMessage, result.message)
    }
}