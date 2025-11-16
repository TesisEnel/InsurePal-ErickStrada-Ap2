package edu.ucne.InsurePal.presentation

import edu.ucne.InsurePal.data.Resource
import edu.ucne.InsurePal.domain.Usuario
import edu.ucne.InsurePal.domain.useCases.obtenerUsuarioUseCase
import edu.ucne.InsurePal.domain.useCases.obtenerUsuariosUseCase
import edu.ucne.InsurePal.domain.useCases.saveUsuarioUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.jupiter.api.Assertions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.RegisterExtension


@ExperimentalCoroutinesApi
class UsuarioViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var guardar: saveUsuarioUseCase
    private lateinit var obtener: obtenerUsuarioUseCase
    private lateinit var obtenerLista: obtenerUsuariosUseCase

    private lateinit var viewModel: UsuarioViewModel

    @BeforeEach
    fun setUp() {
        guardar = mockk()
        obtener = mockk()
        obtenerLista = mockk()

        coEvery { obtenerLista() } returns flowOf(Resource.Success(emptyList()))

        viewModel = UsuarioViewModel(guardar, obtener, obtenerLista)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `init carga usuarios y actualiza el estado a éxito`() = runTest {
        val fakeList = listOf(Usuario(1, "User1", "pass"))
        coEvery { obtenerLista() } returns flowOf(Resource.Success(fakeList))

        val vm = UsuarioViewModel(guardar, obtener, obtenerLista)

        assertFalse(vm.state.value.isLoading)
        assertEquals(fakeList, vm.state.value.usuarios)
        coVerify(exactly = 1) { obtenerLista() }
    }

    @Test
    fun `init carga usuarios y actualiza el estado a error`() = runTest {
        val errorMsg = "Error de red"
        coEvery { obtenerLista() } returns flowOf(Resource.Error(errorMsg))

        val vm = UsuarioViewModel(guardar, obtener, obtenerLista)

        assertFalse(vm.state.value.isLoading)
        assertEquals(errorMsg, vm.state.value.userMessage)
        assertTrue(vm.state.value.usuarios.isEmpty())
    }

    @Test
    fun `onEvent(crear) con éxito guarda, limpia formulario y recarga usuarios`() = runTest {
        val newUser = Usuario(0, "New", "pass")
        val createdUser = Usuario(1, "New", "pass")

        coEvery { guardar(0, newUser) } returns Resource.Success(createdUser)
        coEvery { obtenerLista() } returnsMany listOf(
            flowOf(Resource.Success(emptyList())),
            flowOf(Resource.Success(listOf(createdUser)))
        )

        viewModel = UsuarioViewModel(guardar, obtener, obtenerLista)

        viewModel.onEvent(UsuarioEvent.crear(newUser))

        val state = viewModel.state.value
        assertEquals("Usuario creado", state.userMessage)
        assertEquals(listOf(createdUser), state.usuarios)
        assertNull(state.usuarioId)

        coVerifyOrder {
            obtenerLista()
            guardar(0, newUser)
            obtenerLista()
        }
    }

    @Test
    fun `onEvent(crear) con error muestra mensaje`() = runTest {
        val newUser = Usuario(0, "New", "pass")
        val errorMsg = "Error al guardar"

        coEvery { guardar(0, newUser) } returns Resource.Error(errorMsg)

        viewModel.onEvent(UsuarioEvent.crear(newUser))

        assertEquals("Error al crear el usuario", viewModel.state.value.userMessage)
        coVerify(exactly = 1) { guardar(0, newUser) }
        coVerify(exactly = 1) { obtenerLista() }
    }

    @Test
    fun `onEvent(actualizar) con éxito guarda, limpia formulario y recarga usuarios`() = runTest {
        val updatedUser = Usuario(1, "Updated", "pass")

        coEvery { guardar(updatedUser.usuarioId, updatedUser) } returns Resource.Success(updatedUser)
        coEvery { obtenerLista() } returnsMany listOf(
            flowOf(Resource.Success(emptyList())),
            flowOf(Resource.Success(listOf(updatedUser)))
        )

        viewModel = UsuarioViewModel(guardar, obtener, obtenerLista)

        viewModel.onEvent(UsuarioEvent.actualizar(updatedUser))

        val state = viewModel.state.value
        assertEquals("Usuario actualizado exitosamente", state.userMessage)
        assertEquals(listOf(updatedUser), state.usuarios)
        assertNull(state.usuarioId)

        coVerifyOrder {
            obtenerLista()
            guardar(updatedUser.usuarioId, updatedUser)
            obtenerLista()
        }
    }

    @Test
    fun `onEvent(actualizar) con error muestra mensaje`() = runTest {
        val updatedUser = Usuario(1, "Updated", "pass")
        val errorMsg = "Error al actualizar"

        coEvery { guardar(updatedUser.usuarioId, updatedUser) } returns Resource.Error(errorMsg)

        viewModel.onEvent(UsuarioEvent.actualizar(updatedUser))

        assertEquals("Error al actualizar el usuario", viewModel.state.value.userMessage)
        coVerify(exactly = 1) { guardar(updatedUser.usuarioId, updatedUser) }
    }

    @Test
    fun `onEvent(obtener) con ID válido carga datos en el formulario`() = runTest {
        val user = Usuario(5, "TestUser", "TestPass")
        coEvery { obtener(5) } returns flowOf(Resource.Success(user))

        viewModel.onEvent(UsuarioEvent.obtener(5))

        val state = viewModel.state.value
        assertEquals(user.usuarioId, state.usuarioId)
        assertEquals(user.userName, state.userName)
        assertEquals(user.password, state.password)
    }

    @Test
    fun `onEvent(obtener) con ID nulo`() = runTest {
        val errorMsg = "ID nulo"
        coEvery { obtener(null) } returns flowOf(Resource.Error(errorMsg))

        viewModel.onEvent(UsuarioEvent.obtener(null))

        assertEquals(errorMsg, viewModel.state.value.userMessage)
        assertEquals(null, viewModel.state.value.usuarioId)
        coVerify(exactly = 1) { obtener(null) }
    }

    @Test
    fun `onEvent(onUsernameChange) actualiza el estado`() {
        val newUsername = "nuevo_usuario"
        viewModel.onEvent(UsuarioEvent.onUsernameChange(newUsername))
        assertEquals(newUsername, viewModel.state.value.userName)
    }

    @Test
    fun `onEvent(onPasswordChange) actualiza el estado`() {
        val newPassword = "nueva_clave"
        viewModel.onEvent(UsuarioEvent.onPasswordChange(newPassword))
        assertEquals(newPassword, viewModel.state.value.password)
    }

    @Test
    fun `onEvent(userMessageShown) limpia el mensaje`() = runTest {
        coEvery { obtener(99) } returns flowOf(Resource.Error("Error"))

        viewModel.onEvent(UsuarioEvent.obtener(99))
        assertNotNull(viewModel.state.value.userMessage)

        viewModel.onEvent(UsuarioEvent.userMessageShown)

        assertNull(viewModel.state.value.userMessage)
    }

    @Test
    fun `onEvent(new) limpia el usuarioId`() = runTest {
        val user = Usuario(5, "TestUser", "TestPass")
        coEvery { obtener(5) } returns flowOf(Resource.Success(user))

        viewModel.onEvent(UsuarioEvent.obtener(5))
        assertEquals(5, viewModel.state.value.usuarioId)

        viewModel.onEvent(UsuarioEvent.new)

        assertNull(viewModel.state.value.usuarioId)
    }
}

@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : BeforeEachCallback, AfterEachCallback {

    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun afterEach(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}