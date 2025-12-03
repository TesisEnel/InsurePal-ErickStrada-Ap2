package edu.ucne.InsurePal.presentation.cotizacionVehiculo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.InsurePal.ui.theme.InsurePalTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CotizacionVehiculoScreen(
    viewModel: CotizacionVehiculoViewModel = hiltViewModel(),
    onNavigateToPayment: (Double, String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val onBackAction = {
        viewModel.onEvent(CotizacionVehiculoEvent.OnVolverClick)
        onNavigateBack()
    }

    BackHandler(onBack = onBackAction)

    CotizacionVehiculoContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToPayment = onNavigateToPayment,
        onNavigateBack = onBackAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CotizacionVehiculoContent(
    state: CotizacionVehiculoUiState,
    onEvent: (CotizacionVehiculoEvent) -> Unit,
    onNavigateToPayment: (Double, String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle de Cotización") },
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
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            onEvent(CotizacionVehiculoEvent.OnContinuarPagoClick)

                            onNavigateToPayment(state.totalPagar, state.vehiculoDescripcion)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = MaterialTheme.shapes.medium,
                        enabled = !state.isLoading && state.error == null
                    ) {
                        Text("Solicitar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onNavigateBack) {
                        Text("Regresar")
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    InfoSectionCard(
                        title = "Vehículo a Asegurar",
                        icon = Icons.Default.DirectionsCar
                    ) {
                        FilaDetalle("Descripción", state.vehiculoDescripcion)
                        FilaDetalle("Valor Mercado", "RD$ ${formatearMoneda(state.valorMercado)}")
                        FilaDetalle("Cobertura", state.cobertura)
                    }

                    InfoSectionCard(
                        title = "Desglose de Prima (Mensual)",
                        icon = Icons.Default.AttachMoney,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        FilaDetalle("Prima Neta", "RD$ ${formatearMoneda(state.primaNeta)}")
                        FilaDetalle("Impuestos (18%)", "RD$ ${formatearMoneda(state.impuestos)}")

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total a Pagar",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "RD$ ${formatearMoneda(state.totalPagar)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Text(
                        text = "Al continuar, usted acepta los términos y condiciones de la póliza seleccionada. Los montos están expresados en Pesos Dominicanos (DOP).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoSectionCard(
    title: String,
    icon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            content()
        }
    }
}

@Composable
fun FilaDetalle(label: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

fun formatearMoneda(cantidad: Double): String {
    val format = NumberFormat.getNumberInstance(Locale.US)
    format.minimumFractionDigits = 2
    format.maximumFractionDigits = 2
    return format.format(cantidad)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CotizacionVehiculoScreenPreview() {
    InsurePalTheme {
        CotizacionVehiculoContent(
            state = CotizacionVehiculoUiState(
                isLoading = false,
                vehiculoDescripcion = "Toyota Corolla 2024",
                valorMercado = 1500000.0,
                cobertura = "Full Cobertura",
                primaNeta = 37500.0,
                impuestos = 6750.0,
                totalPagar = 44250.0
            ),
            onEvent = {},
            onNavigateToPayment = { _, _ -> },
            onNavigateBack = {}
        )
    }
}