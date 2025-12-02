package edu.ucne.InsurePal.presentation.polizas.vida.reclamoVida

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import edu.ucne.InsurePal.ui.theme.InsurePalTheme
import java.io.File
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReclamoVidaScreen(
    viewModel: ReclamoVidaViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    onReclamoSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var polizaIdInput by remember { mutableStateOf("") }
    var polizaIdError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    ReclamoEffects(state, viewModel, onReclamoSuccess)

    ReclamoDatePickerDialog(
        show = showDatePicker,
        onDismiss = { showDatePicker = false },
        onDateSelected = { date ->
            viewModel.onEvent(ReclamoVidaEvent.FechaFallecimientoChanged(date))
        }
    )

    Scaffold(
        topBar = { ReclamoTopBar(navigateBack) }
    ) { paddingValues ->
        ReclamoVidaBody(
            paddingValues = paddingValues,
            state = state,
            polizaIdInput = polizaIdInput,
            polizaIdError = polizaIdError,
            onPolizaIdChange = {
                polizaIdInput = it
                if (polizaIdError != null) polizaIdError = null
            },
            onDateClick = { showDatePicker = true },
            onEvent = viewModel::onEvent,
            onEnviarClick = {
                if (polizaIdInput.isBlank()) {
                    polizaIdError = "El ID de la póliza es obligatorio"
                } else {
                    polizaIdError = null
                    viewModel.onEvent(ReclamoVidaEvent.GuardarReclamo(polizaIdInput, 0))
                }
            }
        )
    }
}

@Composable
private fun ReclamoEffects(
    state: ReclamoVidaUiState,
    viewModel: ReclamoVidaViewModel,
    onReclamoSuccess: () -> Unit
) {
    LaunchedEffect(state.esExitoso) {
        if (state.esExitoso) {
            onReclamoSuccess()
        }
    }
    LaunchedEffect(state.error) {
        state.error?.let {
            viewModel.onEvent(ReclamoVidaEvent.ErrorVisto)
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
        title = { Text("Reclamo Seguro de Vida") },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
private fun ReclamoVidaBody(
    paddingValues: PaddingValues,
    state: ReclamoVidaUiState,
    polizaIdInput: String,
    polizaIdError: String?,
    onPolizaIdChange: (String) -> Unit,
    onDateClick: () -> Unit,
    onEvent: (ReclamoVidaEvent) -> Unit,
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
            text = "Datos de la Póliza",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = polizaIdInput,
            onValueChange = onPolizaIdChange,
            label = { Text("ID de la Póliza") },
            placeholder = { Text("Ej: VIDA-102") },
            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            isError = polizaIdError != null,
            supportingText = polizaIdError?.let { { Text(it) } }
        )

        HorizontalDivider()

        Text(
            text = "Información del Deceso",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = state.nombreAsegurado,
            onValueChange = { onEvent(ReclamoVidaEvent.NombreAseguradoChanged(it)) },
            label = { Text("Nombre del Asegurado") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            isError = state.errorNombreAsegurado != null,
            supportingText = state.errorNombreAsegurado?.let { { Text(it) } }
        )

        OutlinedTextField(
            value = state.fechaFallecimiento.take(10),
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha de Fallecimiento") },
            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDateClick() },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            enabled = false,
            isError = state.errorFechaFallecimiento != null,
            supportingText = state.errorFechaFallecimiento?.let { { Text(it) } }
        )

        CausaMuerteDropdown(
            selectedOption = state.causaMuerte,
            onOptionSelected = { onEvent(ReclamoVidaEvent.CausaMuerteChanged(it)) },
            isError = state.errorCausaMuerte != null,
            errorMessage = state.errorCausaMuerte
        )

        OutlinedTextField(
            value = state.lugarFallecimiento,
            onValueChange = { onEvent(ReclamoVidaEvent.LugarFallecimientoChanged(it)) },
            label = { Text("Lugar de Fallecimiento") },
            placeholder = { Text("Ej: Hospital General, Casa") },
            leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            isError = state.errorLugarFallecimiento != null,
            supportingText = state.errorLugarFallecimiento?.let { { Text(it) } }
        )

        OutlinedTextField(
            value = state.numCuenta,
            onValueChange = { onEvent(ReclamoVidaEvent.NumCuentaChanged(it)) },
            label = { Text("Cuenta Bancaria (Beneficiario)") },
            placeholder = { Text("Para depósito de indemnización") },
            leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
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
            onValueChange = { onEvent(ReclamoVidaEvent.DescripcionChanged(it)) },
            label = { Text("Descripción / Observaciones") },
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
            text = "Documentación Requerida",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        DocumentSelector(
            selectedFile = state.archivoActa,
            label = "Subir Acta de Defunción",
            onFileSelected = { file ->
                onEvent(ReclamoVidaEvent.ActaDefuncionSeleccionada(file))
            },
            isError = state.errorArchivoActa != null
        )

        if (state.errorArchivoActa != null) {
            Text(
                text = state.errorArchivoActa,
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
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Procesando...")
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
fun CausaMuerteDropdown(
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val options = listOf("Muerte Natural", "Accidente", "Enfermedad", "Homicidio", "Suicidio", "Otro")
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
            label = { Text("Causa de Muerte") },
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

@Composable
fun DocumentSelector(
    selectedFile: File?,
    label: String,
    onFileSelected: (File) -> Unit,
    isError: Boolean = false
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val file = context.crearArchivoTemporal(it)
            if (file != null) {
                onFileSelected(file)
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isError) BorderStroke(2.dp, MaterialTheme.colorScheme.error) else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (selectedFile != null) {
                AsyncImage(
                    model = selectedFile,
                    contentDescription = "Documento seleccionado",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Documento cargado",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "(Toque para cambiar)",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "(Obligatorio)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

fun Context.crearArchivoTemporal(uri: Uri): File? {
    return try {
        val stream = contentResolver.openInputStream(uri)
        val file = File.createTempFile("doc_vida_", ".jpg", cacheDir)
        stream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file
    } catch (e: Exception) {
        null
    }
}

@Preview(showSystemUi = true)
@Composable
fun ReclamoVidaScreenPreview() {
    InsurePalTheme {
        ReclamoVidaScreen(
            navigateBack = {},
            onReclamoSuccess = {}
        )
    }
}