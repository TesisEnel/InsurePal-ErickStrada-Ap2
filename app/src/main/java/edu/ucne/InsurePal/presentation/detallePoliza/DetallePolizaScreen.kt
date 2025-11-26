package edu.ucne.InsurePal.presentation.detallePoliza

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.InsurePal.ui.theme.InsurePalTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DetallePolizaScreen(
    viewModel: DetallePolizaViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToPago: (Double, String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) onNavigateBack()
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(DetallePolizaEvent.OnErrorDismiss)
        }
    }
    PolicyDetailContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        onNavigateToPago = onNavigateToPago,
        snackbarHostState = snackbarHostState,
        policyType = viewModel.policyType
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PolicyDetailContent(
    state: DetallePolizaUiState,
    onEvent: (DetallePolizaEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToPago: (Double, String) -> Unit,
    snackbarHostState: SnackbarHostState,
    policyType: String
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPlanSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle de Póliza") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HeaderCard(state)

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Información de la Póliza",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        state.details.forEach { (label, value) ->
                            DetailRow(label, value)
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (policyType == "VEHICULO") {
                    OutlinedButton(
                        onClick = { showPlanSheet = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cambiar Tipo de Cobertura")
                    }
                }

                if (!state.isPaid) {
                    Button(
                        onClick = {
                            onNavigateToPago(state.price, "Pago de ${state.title}")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pagar RD$ ${formatMoney(state.price)}")
                    }
                } else {
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Certificado de Cobertura")
                    }
                }

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar Póliza")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar Póliza?") },
            text = { Text("Esta acción es permanente y perderás la cobertura inmediatamente.") },
            confirmButton = {
                TextButton(onClick = {
                    onEvent(DetallePolizaEvent.OnEliminarPoliza)
                    showDeleteDialog = false
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showPlanSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPlanSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Selecciona nueva cobertura", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                val planes = listOf("Cobertura Full", "Ley", "Daños a Terceros")
                planes.forEach { plan ->
                    val selected = plan == state.coverageType
                    ListItem(
                        headlineContent = { Text(plan) },
                        leadingContent = { RadioButton(selected = selected, onClick = null) },
                        modifier = Modifier.clickable {
                            onEvent(DetallePolizaEvent.OnCambiarPlanVehiculo(plan))
                            showPlanSheet = false
                        }
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun HeaderCard(state: DetallePolizaUiState) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = state.subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        text = state.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Surface(
                    color = if (state.isPaid) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                    shape = CircleShape
                ) {
                    Text(
                        text = state.status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Prima: RD$ ${formatMoney(state.price)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

fun formatMoney(amount: Double): String {
    return NumberFormat.getNumberInstance(Locale.US).format(amount)
}

// 3. PREVIEWS DE EJEMPLO
@Preview(name = "Detalle Vehículo Pendiente", showSystemUi = true)
@Composable
fun PolicyDetailVehiclePreview() {
    InsurePalTheme {
        PolicyDetailContent(
            state = DetallePolizaUiState(
                title = "Toyota Corolla 2024",
                subtitle = "2024 • Rojo",
                status = "Cotizando",
                price = 25000.0,
                isPaid = false,
                coverageType = "Cobertura Full",
                details = mapOf(
                    "Placa" to "A-458291",
                    "Chasis" to "8291SKL291",
                    "Tipo Cobertura" to "Cobertura Full",
                    "Vigencia" to "Pendiente"
                )
            ),
            onEvent = {},
            onNavigateBack = {},
            onNavigateToPago = { _, _ -> },
            snackbarHostState = remember { SnackbarHostState() },
            policyType = "VEHICULO"
        )
    }
}

@Preview(name = "Detalle Vida Pagado", showSystemUi = true)
@Composable
fun PolicyDetailLifePreview() {
    InsurePalTheme {
        PolicyDetailContent(
            state = DetallePolizaUiState(
                title = "Juan Pérez",
                subtitle = "Seguro de Vida",
                status = "Activo",
                price = 5500.0,
                isPaid = true,
                details = mapOf(
                    "Beneficiario" to "Maria Pérez",
                    "Cédula Beneficiario" to "402-1111111-1",
                    "Monto Cobertura" to "RD$ 1,000,000",
                    "Ocupación" to "Oficinista"
                )
            ),
            onEvent = {},
            onNavigateBack = {},
            onNavigateToPago = { _, _ -> },
            snackbarHostState = remember { SnackbarHostState() },
            policyType = "VIDA"
        )
    }
}