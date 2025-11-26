package edu.ucne.InsurePal.domain

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.data.toRequest
import edu.ucne.InsurePal.domain.usuario.model.Usuario
import edu.ucne.InsurePal.domain.usuario.repository.UsuarioRepository
import edu.ucne.InsurePal.domain.usuario.useCases.SaveUsuarioUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class SaveUsuarioUseCaseTest {

    private lateinit var repository: UsuarioRepository
    private lateinit var useCase: SaveUsuarioUseCase


    private val testUsuario = Usuario(1, "testUser", "123")
    private val testUsuarioRequest = testUsuario.toRequest()

    @Before
    fun setUp() {
        repository = mockk()
        useCase = SaveUsuarioUseCase(repository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `invoke con id 0 (crear) devuelve Success si el repositorio tiene éxito`() = runTest {
        val newUsuario = Usuario(0, "newUser", "123")
        val newUsuarioRequest = newUsuario.toRequest()
        val createdUsuario = Usuario(10, "newUser", "123")
        val repoSuccess = Resource.Success(createdUsuario)

        coEvery { repository.postUsuario(newUsuarioRequest) } returns repoSuccess

        val result = useCase(0, newUsuario)

        assertTrue(result is Resource.Success)
        assertEquals(createdUsuario, result.data)
        coVerify(exactly = 1) { repository.postUsuario(newUsuarioRequest) }
        coVerify(exactly = 0) { repository.putUsuario(any(), any()) }
    }

    @Test
    fun `invoke con id 0 (crear) devuelve Error si el repositorio falla`() = runTest {
        val newUsuario = Usuario(0, "newUser", "123")
        val newUsuarioRequest = newUsuario.toRequest()
        val errorMessage = "Error al crear"
        val repoError = Resource.Error<Usuario>(errorMessage)

        coEvery { repository.postUsuario(newUsuarioRequest) } returns repoError

        val result = useCase(0, newUsuario)

        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, result.message)
        coVerify(exactly = 1) { repository.postUsuario(newUsuarioRequest) }
    }

    @Test
    fun `invoke con id distinto de 0 (actualizar) devuelve Success si el repositorio tiene éxito`() = runTest {
        val updateId = 5
        val updatedUsuario = Usuario(5, "updatedUser", "123")
        val updatedUsuarioRequest = updatedUsuario.toRequest()
        val repoSuccess = Resource.Success(Unit)

        coEvery { repository.putUsuario(updateId, updatedUsuarioRequest) } returns repoSuccess

        val result = useCase(updateId, updatedUsuario)

        assertTrue(result is Resource.Success)
        assertEquals(updatedUsuario, result.data)
        coVerify(exactly = 1) { repository.putUsuario(updateId, updatedUsuarioRequest) }
        coVerify(exactly = 0) { repository.postUsuario(any()) }
    }

    @Test
    fun `invoke con id distinto de 0 (actualizar) devuelve Error si el repositorio falla`() = runTest {
        val updateId = 5
        val updatedUsuario = Usuario(5, "updatedUser", "123")
        val updatedUsuarioRequest = updatedUsuario.toRequest()
        val errorMessage = "Error al actualizar"
        val repoError = Resource.Error<Unit>(errorMessage)

        coEvery { repository.putUsuario(updateId, updatedUsuarioRequest) } returns repoError

        val result = useCase(updateId, updatedUsuario)

        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, result.message)
        coVerify(exactly = 1) { repository.putUsuario(updateId, updatedUsuarioRequest) }
    }
}