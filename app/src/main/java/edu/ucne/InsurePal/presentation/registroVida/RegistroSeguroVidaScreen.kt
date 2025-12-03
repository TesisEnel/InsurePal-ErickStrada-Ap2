package edu.ucne.InsurePal.presentation.registroVida

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.InsurePal.presentation.components.DatePickerField
import edu.ucne.InsurePal.presentation.components.SectionHeader
import edu.ucne.InsurePal.presentation.components.AppDropdown
import edu.ucne.InsurePal.ui.theme.InsurePalTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun RegistroSeguroVidaScreen(
    viewModel: SeguroVidaViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess && state.cotizacionIdCreada != null) {
            onNavigateToHome()
            viewModel.onEvent(SeguroVidaEvent.OnNavegacionFinalizada)
        }
    }

    LaunchedEffect(state.errorGlobal) {
        state.errorGlobal?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(SeguroVidaEvent.OnErrorDismiss)
        }
    }

    SeguroVidaContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeguroVidaContent(
    state: SeguroVidaUiState,
    onEvent: (SeguroVidaEvent) -> Unit,
    onNavigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nuevo Seguro de Vida") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            CotizacionBottomBar(
                prima = state.primaCalculada,
                isLoading = state.isLoading,
                onCotizar = { onEvent(SeguroVidaEvent.OnCotizarClick) }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            SectionHeader("Datos del Asegurado")

            OutlinedTextField(
                value = state.nombres,
                onValueChange = { onEvent(SeguroVidaEvent.OnNombresChanged(it)) },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.errorNombres != null,
                supportingText = state.errorNombres?.let { { Text(it) } },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )

            OutlinedTextField(
                value = state.cedula,
                onValueChange = { onEvent(SeguroVidaEvent.OnCedulaChanged(it)) },
                label = { Text("Cédula (Sin guiones)") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.errorCedula != null,
                supportingText = state.errorCedula?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                trailingIcon = {
                    if (state.cedula.length == 11) Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                }
            )

            DatePickerField(
                label = "Fecha de Nacimiento",
                fechaSeleccionada = state.fechaNacimiento,
                onFechaChange = { onEvent(SeguroVidaEvent.OnFechaNacimientoChanged(it)) },
                isError = state.errorFechaNacimiento != null,
                errorMessage = state.errorFechaNacimiento
            )

            AppDropdown(
                label = "Ocupación",
                items = SeguroVidaDefaults.Ocupaciones,
                selectedItem = state.ocupacion,
                isError = state.errorOcupacion != null,
                errorMessage = state.errorOcupacion,
                onItemSelected = { onEvent(SeguroVidaEvent.OnOcupacionChanged(it)) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), MaterialTheme.shapes.medium)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("¿Es fumador?", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Esto influye en su prima de riesgo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = state.esFumador,
                    onCheckedChange = { onEvent(SeguroVidaEvent.OnFumadorChanged(it)) }
                )
            }

            HorizontalDivider()

            SectionHeader("Datos del Beneficiario")

            OutlinedTextField(
                value = state.nombreBeneficiario,
                onValueChange = { onEvent(SeguroVidaEvent.OnNombreBeneficiarioChanged(it)) },
                label = { Text("Nombre del Beneficiario") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.errorNombreBeneficiario != null,
                supportingText = state.errorNombreBeneficiario?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.cedulaBeneficiario,
                    onValueChange = { onEvent(SeguroVidaEvent.OnCedulaBeneficiarioChanged(it)) },
                    label = { Text("Cédula") },
                    modifier = Modifier.weight(1f),
                    isError = state.errorCedulaBeneficiario != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Box(modifier = Modifier.weight(1f)) {
                    AppDropdown(
                        label = "Parentesco",
                        items = SeguroVidaDefaults.Parentescos,
                        selectedItem = state.parentesco,
                        isError = state.errorParentesco != null,
                        errorMessage = state.errorParentesco,
                        onItemSelected = { onEvent(SeguroVidaEvent.OnParentescoChanged(it)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            HorizontalDivider()

            SectionHeader("Cobertura Deseada")

            OutlinedTextField(
                value = state.montoCobertura,
                onValueChange = { onEvent(SeguroVidaEvent.OnMontoCoberturaChanged(it)) },
                label = { Text("Suma Asegurada (RD$)") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.errorMontoCobertura != null,
                supportingText = {
                    if (state.errorMontoCobertura != null) {
                        Text(state.errorMontoCobertura, color = MaterialTheme.colorScheme.error)
                    } else {
                        Text("Máximo asegurado: RD$ 1,000,000")
                    }
                },
                leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun CotizacionBottomBar(
    prima: Double,
    isLoading: Boolean,
    onCotizar: () -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Prima Mensual",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "RD$ ${NumberFormat.getNumberInstance(Locale.US).format(prima)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onCotizar,
                enabled = !isLoading,
                shape = MaterialTheme.shapes.medium
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Contratar")
                }
            }
        }
    }
}

@Preview(showSystemUi = true, name = "Formulario Vacío")
@Composable
fun SeguroVidaPreview() {
    InsurePalTheme {
        SeguroVidaContent(
            state = SeguroVidaUiState(),
            onEvent = {},
            onNavigateBack = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showSystemUi = true, name = "Con Datos y Prima")
@Composable
fun SeguroVidaLlenoPreview() {
    InsurePalTheme {
        SeguroVidaContent(
            state = SeguroVidaUiState(
                nombres = "Juan Pérez",
                cedula = "40212345678",
                montoCobertura = "1000000",
                primaCalculada = 5500.0,
                ocupacion = "Oficinista"
            ),
            onEvent = {},
            onNavigateBack = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}