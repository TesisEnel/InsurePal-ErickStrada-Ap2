package edu.ucne.InsurePal.presentation.usuario
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest


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
                onEvent(UsuarioEvent.onLoginClick)
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
    UsuarioScreenContent(
        padding = PaddingValues(0.dp),
        uiState = normalState,
        onEvent = {}
    )
}