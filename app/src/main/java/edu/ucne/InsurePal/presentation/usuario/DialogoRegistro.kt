package edu.ucne.InsurePal.presentation.usuario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Registrar nuevo usuario",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp) // Espaciado uniforme
            ) {
                OutlinedTextField(
                    value = uiState.regUserName,
                    onValueChange = { onEvent(UsuarioEvent.OnRegUsernameChange(it)) },
                    label = { Text("Nombre de Usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp)
                )


                OutlinedTextField(
                    value = uiState.regPassword,
                    onValueChange = { onEvent(UsuarioEvent.OnRegPasswordChange(it)) },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = uiState.regConfirmPassword,
                    onValueChange = { onEvent(UsuarioEvent.OnRegConfirmPasswordChange(it)) },
                    label = { Text("Confirmar Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    isError = uiState.regPassword != uiState.regConfirmPassword,
                    supportingText = {
                        if (uiState.regPassword != uiState.regConfirmPassword) {
                            Text("Las contraseñas no coinciden")
                        }
                    }
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
                        uiState.regPassword == uiState.regConfirmPassword,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onEvent(UsuarioEvent.hideRegistrationDialog) },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(name = "Diálogo Registro - Normal", group = "Dialogos")
@Composable
fun RegistrationDialogPreviewNormal() {
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