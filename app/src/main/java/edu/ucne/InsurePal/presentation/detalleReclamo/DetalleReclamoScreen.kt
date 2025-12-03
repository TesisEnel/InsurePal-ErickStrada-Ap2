package edu.ucne.InsurePal.presentation.detalleReclamo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import edu.ucne.InsurePal.domain.reclamoVehiculo.model.ReclamoVehiculo
import edu.ucne.InsurePal.domain.reclamoVida.model.ReclamoVida
import edu.ucne.InsurePal.presentation.UiModels.TipoReclamo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleReclamoScreen(
    viewModel: DetalleReclamoViewModel = hiltViewModel(),
    isAdmin: Boolean = false,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showRechazoDialog by remember { mutableStateOf(false) }

    val isPendiente = when (state.tipo) {
        TipoReclamo.VEHICULO -> state.reclamoVehiculo?.status == "PENDIENTE"
        TipoReclamo.VIDA -> state.reclamoVida?.status == "PENDIENTE"
        else -> false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle del Reclamo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            if (isAdmin && isPendiente) {
                AdminActionsBar(
                    isUpdating = state.isUpdating,
                    onAprobar = { viewModel.onEvent(DetalleReclamoEvent.OnAprobar) },
                    onRechazar = { showRechazoDialog = true }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                ErrorView(
                    msg = state.error!!,
                    onRetry = { viewModel.onEvent(DetalleReclamoEvent.OnReintentar) },
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                when (state.tipo) {
                    TipoReclamo.VEHICULO -> {
                        state.reclamoVehiculo?.let { reclamo ->
                            ContentDetalleVehiculo(reclamo)
                        } ?: EmptyView("No se encontraron datos del reclamo de vehículo.")
                    }
                    TipoReclamo.VIDA -> {
                        state.reclamoVida?.let { reclamo ->
                            ContentDetalleVida(reclamo)
                        } ?: EmptyView("No se encontraron datos del reclamo de vida.")
                    }
                    TipoReclamo.OTRO -> {
                        EmptyView("Tipo de reclamo no soportado.")
                    }
                }
            }

            state.exitoOperacion?.let { msg ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    containerColor = Color(0xFF2E7D32),
                    contentColor = Color.White
                ) {
                    Text(msg)
                }
            }
        }
    }

    if (showRechazoDialog) {
        RechazoDialog(
            onDismiss = { showRechazoDialog = false },
            onConfirm = { motivo ->
                viewModel.onEvent(DetalleReclamoEvent.OnRechazar(motivo))
                showRechazoDialog = false
            }
        )
    }
}

@Composable
fun ContentDetalleVehiculo(reclamo: ReclamoVehiculo) {
    val imageUrl = reclamo.imagenUrl

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EstadoHeader(
            status = reclamo.status,
            motivoRechazo = reclamo.motivoRechazo
        )

        Text("Evidencia Fotográfica", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        DocumentoCard(url = imageUrl, descripcion = "Evidencia del accidente")

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Detalles del Siniestro", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))

                InfoRow(Icons.Default.Event, "Fecha", reclamo.fechaIncidente.take(10))
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(Icons.Default.LocationOn, "Ubicación", reclamo.direccion)
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(Icons.Default.CarCrash, "Tipo Incidente", reclamo.tipoIncidente)
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(Icons.Default.CreditCard, "Cta. Depósito", reclamo.numCuenta)

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Text("Descripción", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reclamo.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Póliza Asociada", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(reclamo.polizaId, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun ContentDetalleVida(reclamo: ReclamoVida) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EstadoHeader(
            status = reclamo.status,
            motivoRechazo = reclamo.motivoRechazo
        )

        Text("Documentación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        // Acta de Defunción (Obligatorio)
        DocumentoCard(url = reclamo.actaDefuncionUrl, descripcion = "Acta de Defunción")
        Spacer(modifier = Modifier.height(8.dp))
        DocumentoCard(url = reclamo.identificacionUrl, descripcion = "Identificación (Cédula)")

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Información del Deceso", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))

                InfoRow(Icons.Default.Person, "Asegurado", reclamo.nombreAsegurado)
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(Icons.Default.Event, "Fecha Fallecimiento", reclamo.fechaFallecimiento.take(10))
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(Icons.Default.MedicalServices, "Causa", reclamo.causaMuerte)
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(Icons.Default.Place, "Lugar", reclamo.lugarFallecimiento)
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(Icons.Default.AccountBalance, "Cta. Beneficiario", reclamo.numCuenta)

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Text("Observaciones", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reclamo.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Póliza de Vida", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(reclamo.polizaId, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun DocumentoCard(url: String?, descripcion: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        if (!url.isNullOrBlank()) {
            Box(modifier = Modifier.fillMaxSize()) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .crossfade(true)
                        .build(),
                    contentDescription = descripcion,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    error = {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.errorContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.BrokenImage,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    "Error al cargar documento",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                )

                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = descripcion,
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                    Text("Documento no disponible", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun AdminActionsBar(
    isUpdating: Boolean,
    onAprobar: () -> Unit,
    onRechazar: () -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onRechazar,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.weight(1f),
                enabled = !isUpdating
            ) {
                Text("Rechazar")
            }

            Button(
                onClick = onAprobar,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                modifier = Modifier.weight(1f),
                enabled = !isUpdating
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text("Aprobar")
                }
            }
        }
    }
}

@Composable
fun RechazoDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var motivo by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rechazar Reclamo") },
        text = {
            Column {
                Text("Indica el motivo del rechazo para notificar al usuario:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = motivo,
                    onValueChange = {
                        motivo = it
                        isError = false
                    },
                    label = { Text("Motivo") },
                    isError = isError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (isError) {
                    Text("El motivo es obligatorio", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (motivo.isBlank()) {
                        isError = true
                    } else {
                        onConfirm(motivo)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Confirmar Rechazo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun EstadoHeader(status: String, motivoRechazo: String?) {
    val statusUpper = status.uppercase()

    val (bgColor, contentColor, icon) = when (statusUpper) {
        "APROBADO" -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            Icons.Default.CheckCircle
        )
        "RECHAZADO" -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            Icons.Default.Cancel
        )
        else -> Triple(
            Color(0xFFFFF3E0),
            Color(0xFFEF6C00),
            Icons.Default.Schedule
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = statusUpper,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    if (statusUpper == "PENDIENTE") {
                        Text(
                            text = "Este caso está siendo evaluado por un ajustador.",
                            style = MaterialTheme.typography.bodySmall,
                            color = contentColor.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            if (statusUpper == "RECHAZADO" && !motivoRechazo.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = contentColor.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Motivo del rechazo:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = motivoRechazo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun ErrorView(msg: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(msg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

@Composable
fun EmptyView(msg: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(msg, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}