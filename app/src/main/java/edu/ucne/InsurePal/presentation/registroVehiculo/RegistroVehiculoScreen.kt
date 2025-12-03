package edu.ucne.InsurePal.presentation.registroVehiculo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.InsurePal.presentation.components.AppDropdown
import edu.ucne.InsurePal.ui.theme.InsurePalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiculoRegistroScreen(
    viewModel: VehiculoRegistroViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToCotizacion: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess && state.vehiculoIdCreado != null) {
            onNavigateToCotizacion(state.vehiculoIdCreado!!)
            viewModel.onEvent(VehiculoEvent.OnExitoNavegacion)
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(VehiculoEvent.OnMessageShown)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            CotizarFab(
                isLoading = state.isLoading,
                onClick = { viewModel.onEvent(VehiculoEvent.OnGuardarClick) }
            )
        }
    ) { paddingValues ->
        VehiculoRegistroContent(
            state = state,
            onEvent = viewModel::onEvent,
            paddingValues = paddingValues
        )
    }
}

@Composable
fun VehiculoRegistroContent(
    state: VehiculoUiState,
    onEvent: (VehiculoEvent) -> Unit,
    paddingValues: PaddingValues
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        GeneralInfoSection(state, onEvent)

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        IdentificationSection(state, onEvent)

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        CoverageSection(
            selectedCoverage = state.coverageType,
            onCoverageSelected = { onEvent(VehiculoEvent.OnCoverageChanged(it)) }
        )

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun GeneralInfoSection(
    state: VehiculoUiState,
    onEvent: (VehiculoEvent) -> Unit
) {
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
        isError = state.errorName != null,
        supportingText = state.errorName?.let { { Text(it) } },
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
            modifier = Modifier.weight(1f),
            isError = state.errorMarca != null,
            errorMessage = state.errorMarca
        )

        AppDropdown(
            label = "Modelo",
            items = state.modelosDisponibles,
            selectedItem = state.modelo,
            onItemSelected = { onEvent(VehiculoEvent.OnModeloChanged(it)) },
            modifier = Modifier.weight(1f),
            isError = state.errorModelo != null,
            errorMessage = state.errorModelo
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
            isError = state.errorAnio != null,
            supportingText = state.errorAnio?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = state.color,
            onValueChange = { onEvent(VehiculoEvent.OnColorChanged(it)) },
            label = { Text("Color") },
            modifier = Modifier.weight(1.2f),
            singleLine = true,
            isError = state.errorColor != null,
            supportingText = state.errorColor?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
        )
    }
}

@Composable
fun IdentificationSection(
    state: VehiculoUiState,
    onEvent: (VehiculoEvent) -> Unit
) {
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
        isError = state.errorPlaca != null,
        supportingText = state.errorPlaca?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
    )

    OutlinedTextField(
        value = state.chasis,
        onValueChange = { onEvent(VehiculoEvent.OnChasisChanged(it)) },
        label = { Text("Chasis (VIN)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        isError = state.errorChasis != null,
        supportingText = state.errorChasis?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
    )

    OutlinedTextField(
        value = state.valorMercado,
        onValueChange = { onEvent(VehiculoEvent.OnValorChanged(it)) },
        label = { Text("Valor de Mercado (Estimado)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        // Eliminado readOnly=true estricto para permitir correcciones si el usuario lo desea,
        // aunque el VM lo calcula. Puedes volver a ponerlo si prefieres que sea 100% automático.
        prefix = { Text("RD$ ") },
        isError = state.errorValorMercado != null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        supportingText = {
            if (state.errorValorMercado != null) {
                Text(state.errorValorMercado)
            } else if (state.valorMercado.isNotEmpty()) {
                Text("Calculado automáticamente según marca, modelo y año.")
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverageSection(
    selectedCoverage: String,
    onCoverageSelected: (String) -> Unit
) {
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
            val isSelected = selectedCoverage == tipo
            FilterChip(
                selected = isSelected,
                onClick = { onCoverageSelected(tipo) },
                label = { Text(tipo) },
                leadingIcon = if (isSelected) {
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
}

@Composable
fun CotizarFab(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        icon = {
            if (isLoading) {
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
            Text(if (isLoading) "Cotizando..." else "Cotizar")
        }
    )
}

@Preview(name = "Formulario Vacío", showSystemUi = true)
@Composable
fun VehiculoRegistroScreenPreview() {
    InsurePalTheme {
        VehiculoRegistroContent(
            state = VehiculoUiState(),
            onEvent = {},
            paddingValues = PaddingValues(0.dp)
        )
    }
}