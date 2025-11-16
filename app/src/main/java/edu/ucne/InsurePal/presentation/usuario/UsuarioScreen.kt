package edu.ucne.InsurePal.presentation.usuario
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import edu.ucne.InsurePal.domain.Usuario


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioScreen(
    viewModel: UsuarioViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.state.collectLatest { state ->
            state.userMessage?.let { message ->
                snackbarHostState.showSnackbar(message)
                viewModel.onEvent(UsuarioEvent.userMessageShown)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { padding ->

        UsuarioScreenContent(
            padding = padding,
            uiState = uiState,
            onEvent = viewModel::onEvent
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioScreenContent(
    padding: PaddingValues,
    uiState: UsuarioUiState,
    onEvent: (UsuarioEvent) -> Unit
) {

    if (uiState.isDialogVisible) {
        DialogoRegistro(
            uiState = uiState,
            onEvent = onEvent
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = uiState.userName,
            onValueChange = { onEvent(UsuarioEvent.onUsernameChange(it)) },
            label = { Text("Nombre de Usuario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password ?: "",
            onValueChange = { onEvent(UsuarioEvent.onPasswordChange(it)) },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                val usuario = Usuario(
                    usuarioId = 0,
                    userName = uiState.userName,
                    password = uiState.password ?: ""
                )
                onEvent(UsuarioEvent.crear(usuario))
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text(text = "Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                onEvent(UsuarioEvent.showRegistrationDialog)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text(text = "Registrate")
        }


        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UsuarioScreenPreview() {
    val normalState = UsuarioUiState(
        userName = "juan_perez",
        password = "123",
        isLoading = false
    )

    val loadingState = UsuarioUiState(
        userName = "ana_g",
        password = "password123",
        isLoading = true
    )
    UsuarioScreenContent(
        padding = PaddingValues(0.dp),
        uiState = normalState,
        onEvent = {}
    )
}