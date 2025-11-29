package edu.ucne.InsurePal.presentation.polizas.vida.reclamoVida

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReclamoVidaScreen(
    polizaId: String,
    usuarioId: Int,
    viewModel: ReclamoVidaViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(state.esExitoso) {
        if (state.esExitoso) {
            Toast.makeText(context, "Reclamo de vida enviado correctamente", Toast.LENGTH_LONG).show()
            navigateBack()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onEvent(ReclamoVidaEvent.ErrorVisto)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reclamo Seguro de Vida") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Información del Deceso",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = state.nombreAsegurado,
                onValueChange = { viewModel.onEvent(ReclamoVidaEvent.NombreAseguradoChanged(it)) },
                label = { Text("Nombre del Asegurado") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = state.fechaFallecimiento.take(10),
                onValueChange = { viewModel.onEvent(ReclamoVidaEvent.FechaFallecimientoChanged(it)) },
                label = { Text("Fecha de Fallecimiento (YYYY-MM-DD)") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            CausaMuerteDropdown(
                selectedOption = state.causaMuerte,
                onOptionSelected = { viewModel.onEvent(ReclamoVidaEvent.CausaMuerteChanged(it)) }
            )

            OutlinedTextField(
                value = state.lugarFallecimiento,
                onValueChange = { viewModel.onEvent(ReclamoVidaEvent.LugarFallecimientoChanged(it)) },
                label = { Text("Lugar de Fallecimiento") },
                placeholder = { Text("Ej: Hospital General, Casa") },
                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = state.numCuenta,
                onValueChange = { viewModel.onEvent(ReclamoVidaEvent.NumCuentaChanged(it)) },
                label = { Text("Cuenta Bancaria (Beneficiario)") },
                placeholder = { Text("Para depósito de indemnización") },
                leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = state.descripcion,
                onValueChange = { viewModel.onEvent(ReclamoVidaEvent.DescripcionChanged(it)) },
                label = { Text("Descripción / Observaciones") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                )
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
                    viewModel.onEvent(ReclamoVidaEvent.ActaDefuncionSeleccionada(file))
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.onEvent(ReclamoVidaEvent.GuardarReclamo(polizaId, usuarioId))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = state.camposValidos && !state.isLoading,
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CausaMuerteDropdown(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
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
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
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
    onFileSelected: (File) -> Unit
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
        )
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
                            tint = Color.Green,
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
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
            polizaId = "VIDA-123",
            usuarioId = 1,
            navigateBack = {}
        )
    }
}