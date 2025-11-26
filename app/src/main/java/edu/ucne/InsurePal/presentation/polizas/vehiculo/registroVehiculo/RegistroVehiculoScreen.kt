package edu.ucne.InsurePal.presentation.polizas.vehiculo

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
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
import edu.ucne.InsurePal.presentation.polizas.vehiculo.registroVehiculo.VehiculoEvent
import edu.ucne.InsurePal.presentation.polizas.vehiculo.registroVehiculo.VehiculoRegistroViewModel
import edu.ucne.InsurePal.presentation.polizas.vehiculo.registroVehiculo.VehiculoUiState
import edu.ucne.InsurePal.ui.theme.InsurePalTheme

@Composable
fun VehiculoRegistroScreen(
    viewModel: VehiculoRegistroViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToCotizacion: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = state.isSuccess) {
        if (state.isSuccess && state.vehiculoIdCreado != null) {
            Toast.makeText(context, "Vehículo registrado. Generando cotización...", Toast.LENGTH_SHORT).show()
            onNavigateToCotizacion(state.vehiculoIdCreado!!)

            viewModel.onEvent(VehiculoEvent.OnExitoNavegacion)
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
            ExtendedFloatingActionButton(
                onClick = { onEvent(VehiculoEvent.OnGuardarClick) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.AttachMoney, contentDescription = "Cotizar")
                    }
                },
                text = {
                    Text(if (state.isLoading) "Cotizando..." else "Cotizar")
                }
            )
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

                AppDropdown(
                    label = "Marca",
                    items = state.marcasDisponibles,
                    selectedItem = state.marca,
                    onItemSelected = { onEvent(VehiculoEvent.OnMarcaChanged(it)) },
                    modifier = Modifier.weight(1f)
                )

                AppDropdown(
                    label = "Modelo",
                    items = state.modelosDisponibles,
                    selectedItem = state.modelo,
                    onItemSelected = { onEvent(VehiculoEvent.OnModeloChanged(it)) },
                    modifier = Modifier.weight(1f)
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
                onValueChange = { },
                label = { Text("Valor de Mercado (Estimado)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = true,
                prefix = { Text("RD$ ") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                supportingText = {
                    if (state.valorMercado.isNotEmpty()) {
                        Text("Calculado automáticamente según marca, modelo y año.")
                    }
                }
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
                val coberturas = listOf("Cobertura Full", "Ley", "Daños a Terceros")

                coberturas.forEach { tipo ->
                    FilterChip(
                        selected = state.coverageType == tipo,
                        onClick = { onEvent(VehiculoEvent.OnCoverageChanged(tipo)) },
                        label = { Text(tipo) },
                        leadingIcon = if (state.coverageType == tipo) {
                            {
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

@Preview(name = "Formulario Vacío", showSystemUi = true)
@Composable
fun VehiculoRegistroScreenPreview() {
    InsurePalTheme {
        VehiculoRegistroContent(
            state = VehiculoUiState(),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}