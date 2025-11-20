import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.usuario.model.Usuario
import edu.ucne.InsurePal.domain.usuario.repository.UsuarioRepository
import edu.ucne.InsurePal.domain.usuario.useCases.ObtenerUsuarioUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class ObtenerUsuarioUseCaseTest {

    private lateinit var repository: UsuarioRepository
    private lateinit var useCase: ObtenerUsuarioUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = ObtenerUsuarioUseCase(repository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `invoke con ID válido devuelve flow de éxito del repositorio`() = runTest {
        val userId = 1
        val fakeUsuario = Usuario(1, "testUser","123")
        val fakeFlow: Flow<Resource<Usuario>> = flowOf(Resource.Success(fakeUsuario))

        coEvery { repository.getUsuario(userId) } returns fakeFlow

        val resultFlow = useCase(userId)

        assertEquals(fakeFlow, resultFlow)

        val emission = resultFlow.first()
        assertTrue(emission is Resource.Success)
        assertEquals(fakeUsuario, emission.data)

        coVerify(exactly = 1) { repository.getUsuario(userId) }
    }

    @Test
    fun `invoke devuelve flow de error si el repositorio falla`() = runTest {
        val userId = 99
        val errorMessage = "No encontrado"
        val fakeErrorFlow: Flow<Resource<Usuario>> = flowOf(Resource.Error(errorMessage))

        coEvery { repository.getUsuario(userId) } returns fakeErrorFlow

        val resultFlow = useCase(userId)

        val emission = resultFlow.first()
        assertTrue(emission is Resource.Error)
        assertEquals(errorMessage, emission.message)

        coVerify(exactly = 1) { repository.getUsuario(userId) }
    }

    @Test
    fun `invoke con ID nulo pasa null al repositorio y devuelve su flow`() = runTest {
        val userId = null
        val errorMessage = "ID no puede ser nulo"
        val fakeErrorFlow: Flow<Resource<Usuario>> = flowOf(Resource.Error(errorMessage))

        coEvery { repository.getUsuario(userId) } returns fakeErrorFlow

        val resultFlow = useCase(userId)

        val emission = resultFlow.first()
        assertTrue(emission is Resource.Error)
        assertEquals(errorMessage, emission.message)

        coVerify(exactly = 1) { repository.getUsuario(null) }
    }
}