package edu.ucne.InsurePal.presentation.pago.listaPago

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.InsurePal.domain.pago.model.EstadoPago
import edu.ucne.InsurePal.domain.pago.model.Pago
import edu.ucne.InsurePal.presentation.pago.formateo.formatearMoneda
import edu.ucne.InsurePal.ui.theme.InsurePalTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HistorialPagosScreen(
    viewModel: HistorialViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HistorialPagosContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onSincronizar = { viewModel.sincronizar() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialPagosContent(
    state: HistorialPagoUiState,
    onNavigateBack: () -> Unit,
    onSincronizar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Pagos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = onSincronizar) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Sincronizar")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            state.error?.let { errorMsg ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (state.pagos.isEmpty() && !state.isLoading) {
                EmptyState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(state.pagos, key = { it.id }) { pago ->
                        PagoItemCard(pago)
                    }
                }
            }
        }
    }
}

@Composable
fun PagoItemCard(pago: Pago) {
    val isAprobado = pago.estado == EstadoPago.APROBADO
    val colorEstado = if (isAprobado) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
    val containerColor = if (isAprobado) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(colorEstado.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = colorEstado
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if(pago.polizaId.isNotEmpty()) "Póliza: ${pago.polizaId}" else "Pago General",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = pago.fecha.format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "${pago.tarjetaUltimosDigitos} • ${pago.estado}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isAprobado) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                )
            }

            Text(
                text = "RD$ ${formatearMoneda(pago.monto)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isAprobado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.SentimentDissatisfied,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay pagos registrados",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

val pagosDummy = listOf(
    Pago(
        id = 1,
        polizaId = "AUTO-2024-001",
        usuarioId = 1,
        fecha = LocalDateTime.now(),
        monto = 5500.00,
        tarjetaUltimosDigitos = "**** 4242",
        estado = EstadoPago.APROBADO,
        numeroConfirmacion = "TX-123456"
    ),
    Pago(
        id = 2,
        polizaId = "VIDA-999",
        usuarioId = 1,
        fecha = LocalDateTime.now().minusDays(2),
        monto = 2300.50,
        tarjetaUltimosDigitos = "**** 8888",
        estado = EstadoPago.RECHAZADO,
        numeroConfirmacion = "N/A"
    )
)

@Preview(name = "Lista de Pagos", showSystemUi = true)
@Composable
fun HistorialPagosContentPreview() {
    InsurePalTheme {
        HistorialPagosContent(
            state = HistorialPagoUiState(
                pagos = pagosDummy,
                isLoading = false,
                error = null
            ),
            onNavigateBack = {},
            onSincronizar = {}
        )
    }
}

@Preview(name = "Estado Vacío")
@Composable
fun HistorialVacioPreview() {
    InsurePalTheme {
        HistorialPagosContent(
            state = HistorialPagoUiState(
                pagos = emptyList(),
                isLoading = false,
                error = null
            ),
            onNavigateBack = {},
            onSincronizar = {}
        )
    }
}

@Preview(name = "Con Error y Loading")
@Composable
fun HistorialErrorPreview() {
    InsurePalTheme {
        HistorialPagosContent(
            state = HistorialPagoUiState(
                pagos = pagosDummy,
                isLoading = true,
                error = "No hay conexión a internet"
            ),
            onNavigateBack = {},
            onSincronizar = {}
        )
    }
}

@Preview(name = "Item Individual")
@Composable
fun ItemPagoPreview() {
    InsurePalTheme {
        PagoItemCard(pago = pagosDummy[0])
    }
}