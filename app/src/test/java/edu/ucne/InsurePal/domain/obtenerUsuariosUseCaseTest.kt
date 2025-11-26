import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.usuario.model.Usuario
import edu.ucne.InsurePal.domain.usuario.repository.UsuarioRepository
import edu.ucne.InsurePal.domain.usuario.useCases.ObtenerUsuariosUseCase
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

class ObtenerUsuariosUseCaseTest {

    private lateinit var repository: UsuarioRepository
    private lateinit var useCase: ObtenerUsuariosUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = ObtenerUsuariosUseCase(repository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `invoke debe llamar a getUsuarios del repositorio y devolver su flow`() = runTest {
        val fakeUsuario = Usuario(1, "testUser", password = "123")
        val fakeList = listOf(fakeUsuario)
        val fakeFlow: Flow<Resource<List<Usuario>>> = flowOf(Resource.Success(fakeList))

        coEvery { repository.getUsuarios() } returns fakeFlow

        val resultFlow = useCase()

        assertEquals(fakeFlow, resultFlow)

        val emission = resultFlow.first()
        assertTrue(emission is Resource.Success)
        assertEquals(fakeList, emission.data)
        assertEquals("testUser", emission.data?.first()?.userName)

        coVerify(exactly = 1) { repository.getUsuarios() }
    }

    @Test
    fun `invoke debe devolver un flow de error si el repositorio falla`() = runTest {
        val errorMessage = "Error de red"
        val fakeErrorFlow: Flow<Resource<List<Usuario>>> = flowOf(Resource.Error(errorMessage))

        coEvery { repository.getUsuarios() } returns fakeErrorFlow

        val resultFlow = useCase()

        val emission = resultFlow.first()
        assertTrue(emission is Resource.Error)
        assertEquals(errorMessage, emission.message)

        coVerify(exactly = 1) { repository.getUsuarios() }
    }
}