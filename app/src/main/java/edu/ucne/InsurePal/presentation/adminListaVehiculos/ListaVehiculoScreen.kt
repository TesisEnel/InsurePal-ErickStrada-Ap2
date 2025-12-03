package edu.ucne.InsurePal.presentation.adminListaVehiculos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.SeguroVehiculo
import java.text.NumberFormat
import java.util.Locale

private const val pendiente = "Pendiente de pago"
@Composable
fun VehicleListScreen(
    viewModel: VehicleListViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (state.isDetailVisible && state.selectedVehicle != null) {
        VehicleDetailDialog(
            vehicle = state.selectedVehicle!!,
            onDismiss = { viewModel.onEvent(ListaVehiculoEvent.OnDismissDetail) },
            onApprove = {
                viewModel.onEvent(ListaVehiculoEvent.OnUpdateStatus(state.selectedVehicle!!, "Aprobado"))
            },
            onReject = {
                viewModel.onEvent(ListaVehiculoEvent.OnUpdateStatus(state.selectedVehicle!!, "Rechazado"))
            }
        )
    }

    VehicleListContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleListContent(
    state: ListaVehiculoUiState,
    onEvent: (ListaVehiculoEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Vehículos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { onEvent(ListaVehiculoEvent.OnSearchQueryChange(it)) }
            )

            FilterSection(
                showPendingOnly = state.showPendingOnly,
                onToggleFilter = { onEvent(ListaVehiculoEvent.OnTogglePendingFilter) }
            )

            VehicleListContainer(
                isLoading = state.isLoading,
                vehicles = state.filteredVehicles,
                showPendingOnly = state.showPendingOnly,
                onSelectVehicle = { onEvent(ListaVehiculoEvent.OnSelectVehicle(it)) }
            )
        }
    }
}

@Composable
fun FilterSection(
    showPendingOnly: Boolean,
    onToggleFilter: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = showPendingOnly,
            onClick = onToggleFilter,
            label = { Text("Solo Pendientes") },
            leadingIcon = {
                Icon(
                    imageVector = if (showPendingOnly) Icons.Default.Check else Icons.Default.FilterList,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }
}

@Composable
fun VehicleListContainer(
    isLoading: Boolean,
    vehicles: List<SeguroVehiculo>,
    showPendingOnly: Boolean,
    onSelectVehicle: (SeguroVehiculo) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (vehicles.isEmpty()) {
            Text(
                text = if (showPendingOnly) "No hay vehículos pendientes" else "No se encontraron vehículos",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "${vehicles.size} Resultados",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(vehicles) { vehicle ->
                    VehicleItemCard(
                        vehicle = vehicle,
                        onClick = { onSelectVehicle(vehicle) }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Buscar por placa, marca...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Limpiar")
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        singleLine = true
    )
}

@Composable
fun VehicleItemCard(vehicle: SeguroVehiculo, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- MODIFICACIÓN: IMAGEN O ICONO ---
            if (!vehicle.imagenVehiculo.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(vehicle.imagenVehiculo)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto Vehículo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(60.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            // ------------------------------------

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${vehicle.marca} ${vehicle.modelo}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = vehicle.placa,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = vehicle.anio,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StatusChip(status = vehicle.status)
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (color, containerColor) = when(status) {
        "Aprobado", pendiente -> Pair(Color(0xFF2E7D32), Color(0xFFE8F5E9)) // Verdes
        "Rechazado" -> Pair(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer)
        else -> Pair(Color(0xFFEF6C00), Color(0xFFFFF3E0))
    }

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun VehicleDetailDialog(
    vehicle: SeguroVehiculo,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val format = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    // Modificado para mostrar botones en estados iniciales
    val showActions = vehicle.status != "Rechazado" && vehicle.status != "Aprobado" && vehicle.status != "Activo"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // --- MODIFICACIÓN: IMAGEN EN DETALLE ---
                if (!vehicle.imagenVehiculo.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(vehicle.imagenVehiculo)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto Vehículo Grande",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                // ----------------------------------------

                DialogHeader()
                HorizontalDivider()

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailItemRow("Póliza ID", vehicle.idPoliza)
                    DetailItemRow("Placa", vehicle.placa)
                    DetailItemRow("Vehículo", "${vehicle.marca} ${vehicle.modelo} ${vehicle.anio}")
                    DetailItemRow("Color", vehicle.color)
                    DetailItemRow("Chasis", vehicle.chasis)
                    DetailItemRow("Cobertura", vehicle.coverageType)
                    DetailItemRow("Valor Mercado", format.format(vehicle.valorMercado))
                    DetailItemRow("Estado Actual", vehicle.status)
                    DetailItemRow("Pago Realizado", if (vehicle.esPagado) "Sí" else "No")
                }

                Spacer(modifier = Modifier.height(24.dp))

                DialogActions(
                    onReject = onReject,
                    onApprove = onApprove,
                    onDismiss = onDismiss,
                    showDecisionButtons = showActions
                )
            }
        }
    }
}

@Composable
fun DialogHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Revisión Administrativa", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DialogActions(
    onReject: () -> Unit,
    onApprove: () -> Unit,
    onDismiss: () -> Unit,
    showDecisionButtons: Boolean
) {
    if (showDecisionButtons) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onReject,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Rechazar")
            }

            Button(
                onClick = onApprove,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Aprobar")
            }
        }
    }

    TextButton(
        onClick = onDismiss,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(if (showDecisionButtons) "Cancelar" else "Cerrar", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun DetailItemRow(label: String, value: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (value != null) {
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}