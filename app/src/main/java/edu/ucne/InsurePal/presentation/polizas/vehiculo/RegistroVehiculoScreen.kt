package edu.ucne.InsurePal.presentation.polizas.vehiculo.registro // Ajusta el paquete si es necesario

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.InsurePal.presentation.polizas.vehiculo.VehiculoEvent
import edu.ucne.InsurePal.presentation.polizas.vehiculo.VehiculoRegistroViewModel
import edu.ucne.InsurePal.presentation.polizas.vehiculo.VehiculoUiState
import edu.ucne.InsurePal.ui.theme.InsurePalTheme // Asegúrate de importar tu tema


@Composable
fun VehiculoRegistroScreen(
    viewModel: VehiculoRegistroViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = state.isSuccess) {
        if (state.isSuccess) {
            Toast.makeText(context, "Vehículo registrado correctamente", Toast.LENGTH_LONG).show()
            onNavigateBack()
        }
    }

    LaunchedEffect(key1 = state.error) {
        state.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.onEvent(VehiculoEvent.OnMessageShown)
        }
    }

    VehiculoRegistroContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiculoRegistroContent(
    state: VehiculoUiState,
    onEvent: (VehiculoEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Datos del Vehículo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(VehiculoEvent.OnGuardarClick) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Save, contentDescription = "Guardar")
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Información General",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = state.name,
                onValueChange = { onEvent(VehiculoEvent.OnNameChanged(it)) },
                label = { Text("Alias (Ej. Mi Carro)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = state.marca,
                    onValueChange = { onEvent(VehiculoEvent.OnMarcaChanged(it)) },
                    label = { Text("Marca") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )
                OutlinedTextField(
                    value = state.modelo,
                    onValueChange = { onEvent(VehiculoEvent.OnModeloChanged(it)) },
                    label = { Text("Modelo") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = state.anio,
                    onValueChange = { onEvent(VehiculoEvent.OnAnioChanged(it)) },
                    label = { Text("Año") },
                    modifier = Modifier.weight(0.8f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = state.color,
                    onValueChange = { onEvent(VehiculoEvent.OnColorChanged(it)) },
                    label = { Text("Color") },
                    modifier = Modifier.weight(1.2f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            Text(
                text = "Identificación y Valor",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )


            OutlinedTextField(
                value = state.placa,
                onValueChange = { onEvent(VehiculoEvent.OnPlacaChanged(it)) },
                label = { Text("Placa") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
            )

            OutlinedTextField(
                value = state.chasis,
                onValueChange = { onEvent(VehiculoEvent.OnChasisChanged(it)) },
                label = { Text("Chasis (VIN)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
            )

            OutlinedTextField(
                value = state.valorMercado,
                onValueChange = { onEvent(VehiculoEvent.OnValorChanged(it)) },
                label = { Text("Valor de Mercado") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                prefix = { Text("RD$ ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            Text(
                text = "Tipo de Cobertura",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val coberturas = listOf("Full Cobertura", "Ley", "Daños a Terceros")

                coberturas.forEach { tipo ->
                    FilterChip(
                        selected = state.coverageType == tipo,
                        onClick = { onEvent(VehiculoEvent.OnCoverageChanged(tipo)) },
                        label = { Text(tipo) },
                        leadingIcon = if (state.coverageType == tipo) {
                            {
                                // Cambié el icono a Check que tiene más sentido
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// 3. PREVIEWS
@Preview(name = "Formulario Vacío", showSystemUi = true)
@Composable
fun VehiculoRegistroScreenPreview() {
    InsurePalTheme {
        VehiculoRegistroContent(
            state = VehiculoUiState(), // Estado inicial vacío
            onEvent = {}, // Lambda vacía, no hace nada al interactuar
            onNavigateBack = {}
        )
    }
}

@Preview(name = "Formulario Lleno y Cargando", showSystemUi = true)
@Composable
fun VehiculoRegistroScreenFilledPreview() {
    InsurePalTheme {
        VehiculoRegistroContent(
            state = VehiculoUiState(
                isLoading = true, // Probamos el loading en el FAB
                name = "El Consentido",
                marca = "Toyota",
                modelo = "Corolla",
                anio = "2023",
                color = "Rojo",
                placa = "A-123456",
                valorMercado = "1500000",
                coverageType = "Full Cobertura"
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}