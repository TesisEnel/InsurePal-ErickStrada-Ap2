package edu.ucne.InsurePal.presentation.usuario

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.ucne.InsurePal.ui.theme.InsurePalTheme

@Composable
fun DialogoRegistro(
    uiState: UsuarioUiState,
    onEvent: (UsuarioEvent) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onEvent(UsuarioEvent.hideRegistrationDialog) },
        title = { Text("Registrar nuevo usuario") },
        text = {
            Column {
                OutlinedTextField(
                    value = uiState.regUserName,
                    onValueChange = { onEvent(UsuarioEvent.OnRegUsernameChange(it)) },
                    label = { Text("Nombre de Usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.regPassword,
                    onValueChange = { onEvent(UsuarioEvent.OnRegPasswordChange(it)) },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.regConfirmPassword,
                    onValueChange = { onEvent(UsuarioEvent.OnRegConfirmPasswordChange(it)) },
                    label = { Text("Confirmar Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    isError = uiState.regPassword != uiState.regConfirmPassword
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onEvent(UsuarioEvent.registerNewUser)
                },
                enabled = uiState.regUserName.isNotBlank() &&
                        uiState.regPassword.isNotBlank() &&
                        uiState.regPassword == uiState.regConfirmPassword
            ) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onEvent(UsuarioEvent.hideRegistrationDialog) }
            ) {
                Text("Cancelar")
            }
        }
    )
}


@Preview(name = "Diálogo de Registro")
@Composable
fun RegistrationDialogPreview() {
    InsurePalTheme {
        DialogoRegistro(
            uiState = UsuarioUiState(
                regUserName = "nuevo.usuario",
                regPassword = "password123",
                regConfirmPassword = "password123"
            ),
            onEvent = {}
        )
    }
}
