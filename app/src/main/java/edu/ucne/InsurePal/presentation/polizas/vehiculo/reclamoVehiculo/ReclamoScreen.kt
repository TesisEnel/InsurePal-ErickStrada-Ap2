package edu.ucne.InsurePal.presentation.polizas.vehiculo.reclamoVehiculo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.InsurePal.presentation.polizas.ImageSelector
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReclamoScreen(
    polizaId: String,
    usuarioId: Int,
    viewModel: ReclamoViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    onReclamoSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }

    ReclamoEffects(state, viewModel, onReclamoSuccess)

    ReclamoDatePickerDialog(
        show = showDatePicker,
        onDismiss = { showDatePicker = false },
        onDateSelected = { date ->
            viewModel.onEvent(ReclamoEvent.FechaIncidenteChanged(date))
        }
    )

    Scaffold(
        topBar = { ReclamoTopBar(navigateBack) }
    ) { paddingValues ->
        ReclamoBody(
            paddingValues = paddingValues,
            state = state,
            onDateClick = { showDatePicker = true },
            onEvent = viewModel::onEvent,
            onEnviarClick = {
                viewModel.onEvent(ReclamoEvent.GuardarReclamo(polizaId, usuarioId))
            }
        )
    }
}

@Composable
private fun ReclamoEffects(
    state: ReclamoUiState,
    viewModel: ReclamoViewModel,
    onReclamoSuccess: () -> Unit
) {
    LaunchedEffect(state.esExitoso) {
        if (state.esExitoso) {
            onReclamoSuccess()
        }
    }
    LaunchedEffect(state.error) {
        if (state.error != null) {
            viewModel.onEvent(ReclamoEvent.ErrorVisto)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReclamoDatePickerDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    val datePickerState = rememberDatePickerState()
    if (show) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val localDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                        onDateSelected(localDate.toString())
                    }
                    onDismiss()
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReclamoTopBar(navigateBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("Reportar Siniestro") },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun ReclamoBody(
    paddingValues: PaddingValues,
    state: ReclamoUiState,
    onDateClick: () -> Unit,
    onEvent: (ReclamoEvent) -> Unit,
    onEnviarClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Detalles del Incidente",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        TipoIncidenteDropdown(
            selectedOption = state.tipoIncidente,
            onOptionSelected = { onEvent(ReclamoEvent.TipoIncidenteChanged(it)) },
            isError = state.errorTipoIncidente != null,
            errorMessage = state.errorTipoIncidente
        )

        OutlinedTextField(
            value = state.fechaIncidente.take(10),
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha del suceso") },
            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDateClick() },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline
            ),
            enabled = false,
            isError = state.errorFechaIncidente != null,
            supportingText = state.errorFechaIncidente?.let { { Text(it) } }
        )

        OutlinedTextField(
            value = state.direccion,
            onValueChange = { onEvent(ReclamoEvent.DireccionChanged(it)) },
            label = { Text("Ubicación exacta") },
            placeholder = { Text("Ej: Av. Independencia esq. Italia") },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            isError = state.errorDireccion != null,
            supportingText = state.errorDireccion?.let { { Text(it) } }
        )

        OutlinedTextField(
            value = state.numCuenta,
            onValueChange = { onEvent(ReclamoEvent.NumCuentaChanged(it)) },
            label = { Text("Número de Cuenta") },
            placeholder = { Text("Aqui depositaremos") },
            leadingIcon = { Icon(Icons.Default.Money, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            isError = state.errorNumCuenta != null,
            supportingText = state.errorNumCuenta?.let { { Text(it) } }
        )

        OutlinedTextField(
            value = state.descripcion,
            onValueChange = { onEvent(ReclamoEvent.DescripcionChanged(it)) },
            label = { Text("Descripción de los hechos") },
            placeholder = { Text("Describe brevemente cómo ocurrió el accidente...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            isError = state.errorDescripcion != null,
            supportingText = state.errorDescripcion?.let { { Text(it) } }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = "Evidencia Fotográfica",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        ImageSelector(
            selectedFile = state.fotoEvidencia,
            onImageSelected = { file -> onEvent(ReclamoEvent.FotoSeleccionada(file)) },
            isError = state.errorFotoEvidencia != null
        )

        if (state.errorFotoEvidencia != null) {
            Text(
                text = state.errorFotoEvidencia,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onEnviarClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !state.isLoading,
            shape = RoundedCornerShape(12.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enviando...")
            } else {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enviar Reclamo")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoIncidenteDropdown(
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val options = listOf("Choque", "Robo", "Cristal Roto", "Incendio", "Inundación", "Otro")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            readOnly = true,
            value = selectedOption,
            onValueChange = {},
            label = { Text("Tipo de Incidente") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            isError = isError,
            supportingText = errorMessage?.let { { Text(it) } }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onOptionSelected(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

