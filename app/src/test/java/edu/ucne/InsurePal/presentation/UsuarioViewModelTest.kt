package edu.ucne.InsurePal.presentation.usuario

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.usuario.model.Usuario
import edu.ucne.InsurePal.domain.usuario.useCases.ObtenerUsuarioUseCase
import edu.ucne.InsurePal.domain.usuario.useCases.ObtenerUsuariosUseCase
import edu.ucne.InsurePal.domain.usuario.useCases.SaveUsuarioUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class UsuarioViewModelTest {

    private lateinit var guardarUseCase: SaveUsuarioUseCase
    private lateinit var obtenerUseCase: ObtenerUsuarioUseCase
    private lateinit var obtenerListaUseCase: ObtenerUsuariosUseCase

    private lateinit var viewModel: UsuarioViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        guardarUseCase = mock()
        obtenerUseCase = mock()
        obtenerListaUseCase = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init - deberia cargar usuarios exitosamente`() = runTest(testDispatcher) {
        val mockUserList = listOf(Usuario(1, "testUser", "pass"))
        val successFlow = flow {
            emit(Resource.Loading())
            emit(Resource.Success(mockUserList))
        }
        whenever(obtenerListaUseCase()).thenReturn(successFlow)

        viewModel = UsuarioViewModel(guardarUseCase, obtenerUseCase, obtenerListaUseCase)

        assertEquals(true, viewModel.state.value.isLoading)

        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals(mockUserList, state.usuarios)
        assertEquals(null, state.userMessage)
    }

    @Test
    fun `init - deberia manejar error al cargar usuarios`() = runTest(testDispatcher) {
        val errorFlow = flow<Resource<List<Usuario>>> {
            emit(Resource.Loading())
            emit(Resource.Error("Error de prueba"))
        }
        whenever(obtenerListaUseCase()).thenReturn(errorFlow)

        viewModel = UsuarioViewModel(guardarUseCase, obtenerUseCase, obtenerListaUseCase)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals("Error de prueba", state.userMessage)
        assertEquals(emptyList<Usuario>(), state.usuarios)
    }

    @Test
    fun `onEvent crear - deberia guardar usuario y recargar la lista`() = runTest(testDispatcher) {
        val newUser = Usuario(0, "nuevo", "123")
        val createdUser = Usuario(1, "nuevo", "123")
        val refreshedList = listOf(createdUser)

        whenever(obtenerListaUseCase()).thenReturn(flowOf(Resource.Success(emptyList())))

        whenever(guardarUseCase(0, newUser)).thenReturn(Resource.Success<Usuario?>(null))

        whenever(obtenerListaUseCase())
            .thenReturn(flowOf(Resource.Success(emptyList())))
            .thenReturn(flowOf(Resource.Success(refreshedList)))

        viewModel = UsuarioViewModel(guardarUseCase, obtenerUseCase, obtenerListaUseCase)
        advanceUntilIdle()

        assertEquals(emptyList<Usuario>(), viewModel.state.value.usuarios)

        viewModel.onEvent(UsuarioEvent.Crear(newUser))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Usuario creado", state.userMessage)
        assertEquals(refreshedList, state.usuarios)

        verify(guardarUseCase, times(1)).invoke(0, newUser)
        verify(obtenerListaUseCase, times(2)).invoke()
    }

    @Test
    fun `onEvent crear - deberia manejar error al guardar`() = runTest(testDispatcher) {

        val newUser = Usuario(0, "nuevo", "123")

        whenever(obtenerListaUseCase.invoke())
            .thenReturn(flowOf(Resource.Success(emptyList())))


        whenever(guardarUseCase.invoke(0, newUser))
            .thenReturn(Resource.Error("Error al guardar"))

        viewModel = UsuarioViewModel(guardarUseCase, obtenerUseCase, obtenerListaUseCase)
        advanceUntilIdle()


        viewModel.onEvent(UsuarioEvent.Crear(newUser))
        advanceUntilIdle()


        val state = viewModel.state.value
        assertEquals("Error al crear el usuario", state.userMessage)
        assertEquals(emptyList<Usuario>(), state.usuarios)

        verify(guardarUseCase, times(1)).invoke(0, newUser)
        verify(obtenerListaUseCase, times(1)).invoke()
    }


    @Test
    fun `onEvent registerNewUser - deberia fallar si las contraseñas no coinciden`() = runTest(testDispatcher) {
        whenever(obtenerListaUseCase()).thenReturn(flowOf(Resource.Success(emptyList())))

        viewModel = UsuarioViewModel(guardarUseCase, obtenerUseCase, obtenerListaUseCase)
        advanceUntilIdle()

        viewModel.onEvent(UsuarioEvent.OnRegUsernameChange("regUser"))
        viewModel.onEvent(UsuarioEvent.OnRegPasswordChange("pass1"))
        viewModel.onEvent(UsuarioEvent.OnRegConfirmPasswordChange("pass2"))

        viewModel.onEvent(UsuarioEvent.registerNewUser)
        advanceUntilIdle()

        assertEquals("Las contraseñas no coinciden", viewModel.state.value.userMessage)

        verify(guardarUseCase, times(0))
    }

    @Test
    fun `onEvent onUsernameChange - deberia actualizar el userName en el estado`() = runTest(testDispatcher) {
        whenever(obtenerListaUseCase()).thenReturn(flowOf(Resource.Success(emptyList())))
        viewModel = UsuarioViewModel(guardarUseCase, obtenerUseCase, obtenerListaUseCase)
        advanceUntilIdle()

        viewModel.onEvent(UsuarioEvent.OnUsernameChange("nuevoUsuario"))

        assertEquals("nuevoUsuario", viewModel.state.value.userName)
    }
}
