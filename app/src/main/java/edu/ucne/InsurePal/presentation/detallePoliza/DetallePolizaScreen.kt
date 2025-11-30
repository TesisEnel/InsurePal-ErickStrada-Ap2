package edu.ucne.InsurePal.presentation.detallePoliza

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.InsurePal.presentation.pago.formateo.formatearFecha
import edu.ucne.InsurePal.presentation.pago.formateo.formatearMoneda
import edu.ucne.InsurePal.ui.theme.InsurePalTheme

private const val COBERTURA_FULL = "Cobertura Full"

@Composable
fun DetallePolizaScreen(
    viewModel: DetallePolizaViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToPago: (Double, String) -> Unit,
    onNavigateToReclamo: (String, Int) -> Unit
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
        onNavigateToReclamo = onNavigateToReclamo,
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
    onNavigateToReclamo: (String, Int) -> Unit,
    snackbarHostState: SnackbarHostState,
    policyType: String
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPlanSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val isPendingApproval = state.status == "Cotizando" || state.status == "Pendiente de aprobación"

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

                if (isPendingApproval) {
                    PendingApprovalCard()
                }

                PolicyInfoCard(state)

                Spacer(modifier = Modifier.weight(1f))
                PolicyActionsSection(
                    state = state,
                    policyType = policyType,
                    isPendingApproval = isPendingApproval,
                    onNavigateToPago = onNavigateToPago,
                    onNavigateToReclamo = onNavigateToReclamo,
                    onDeleteClick = { showDeleteDialog = true }
                )
            }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                onEvent(DetallePolizaEvent.OnEliminarPoliza)
                showDeleteDialog = false
            }
        )
    }

    if (showPlanSheet) {
        PlanSelectionSheet(
            sheetState = sheetState,
            currentPlan = state.coverageType,
            onDismiss = { showPlanSheet = false },
            onPlanSelected = { plan ->
                onEvent(DetallePolizaEvent.OnCambiarPlanVehiculo(plan))
                showPlanSheet = false
            }
        )
    }
}


@Composable
fun PolicyActionsSection(
    state: DetallePolizaUiState,
    policyType: String,
    isPendingApproval: Boolean,
    onNavigateToPago: (Double, String) -> Unit,
    onNavigateToReclamo: (String, Int) -> Unit,
    onDeleteClick: () -> Unit
) {
    if (policyType == "VEHICULO" && state.isPaid) {
        Button(
            onClick = { onNavigateToReclamo(state.policyId, state.usuarioId) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.Warning, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Realizar Reclamo")
        }
    }
    if (!state.isPaid && !isPendingApproval) {
        Button(
            onClick = { onNavigateToPago(state.price, "Pago de ${state.title}") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.Payment, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pagar ${formatearMoneda(state.price)}")
        }
    } else {
        Spacer(modifier = Modifier.height(8.dp))
    }

    OutlinedButton(
        onClick = onDeleteClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(Icons.Default.Delete, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Eliminar Póliza")
    }
}

@Composable
fun PendingApprovalCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Tu póliza está siendo evaluada por la administración. Te notificaremos cuando sea aprobada para proceder con el pago.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PolicyInfoCard(state: DetallePolizaUiState) {
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
                val valorAMostrar = if (label == "Vigencia") formatearFecha(value) else
                    value
                DetailRow(label, valorAMostrar)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Eliminar Póliza?") },
        text = { Text("Esta acción es permanente y perderás la cobertura inmediatamente.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Eliminar", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanSelectionSheet(
    sheetState: SheetState,
    currentPlan: String,
    onDismiss: () -> Unit,
    onPlanSelected: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Selecciona nueva cobertura", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            val planes = listOf(COBERTURA_FULL, "Ley", "Daños a Terceros")
            planes.forEach { plan ->
                val selected = plan == currentPlan
                ListItem(
                    headlineContent = { Text(plan) },
                    leadingContent = { RadioButton(selected = selected, onClick = null) },
                    modifier = Modifier.clickable { onPlanSelected(plan) }
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
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

                val statusContainerColor = if (state.isPaid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                val statusContentColor = if (state.isPaid) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onError

                Surface(
                    color = statusContainerColor,
                    shape = CircleShape
                ) {
                    Text(
                        text = state.status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusContentColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Prima: ${formatearMoneda(state.price)}",
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
                coverageType = COBERTURA_FULL,
                details = mapOf(
                    "Placa" to "A-458291",
                    "Chasis" to "8291SKL291",
                    "Tipo Cobertura" to COBERTURA_FULL,
                    "Vigencia" to "Pendiente"
                )
            ),
            onEvent = {},
            onNavigateBack = {},
            onNavigateToPago = { _, _ -> },
            onNavigateToReclamo = { _, _ -> }, // Mock navigation
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
            onNavigateToReclamo = { _, _ -> }, // Mock navigation
            snackbarHostState = remember { SnackbarHostState() },
            policyType = "VIDA"
        )
    }
}