package edu.ucne.InsurePal.presentation.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CarCrash
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import edu.ucne.InsurePal.presentation.utils.formatearMoneda

@Composable
fun AdminScreen(
    viewModel: AdminViewModel = hiltViewModel(),
    onNavigateToVehicles: () -> Unit,
    onNavigateToLife: () -> Unit,
    onNavigateToVehicleClaims: () -> Unit,
    onNavigateToLifeClaims: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    AdminContent(
        state = state,
        onReload = { viewModel.loadDashboardData() },
        onNavigateToVehicles = onNavigateToVehicles,
        onNavigateToLife = onNavigateToLife,
        onNavigateToVehicleClaims = onNavigateToVehicleClaims,
        onNavigateToLifeClaims = onNavigateToLifeClaims,
        onLogout = onLogout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminContent(
    state: AdminUiState,
    onReload: () -> Unit,
    onNavigateToVehicles: () -> Unit,
    onNavigateToLife: () -> Unit,
    onNavigateToVehicleClaims: () -> Unit,
    onNavigateToLifeClaims: () -> Unit,
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Panel de Control", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Bienvenido, Administrador", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = onReload) {
                        Icon(Icons.Default.Refresh, "Recargar")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, "Salir", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                FinancialCard(state.totalRevenue)

                Text("Gestión de Pólizas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ModuleCard(
                        title = "Vehículos",
                        icon = Icons.Outlined.DirectionsCar,
                        color = MaterialTheme.colorScheme.primary,
                        count = state.totalVehicles,
                        onClick = onNavigateToVehicles,
                        modifier = Modifier.weight(1f)
                    )
                    ModuleCard(
                        title = "Seguros Vida",
                        icon = Icons.Outlined.HealthAndSafety,
                        color = MaterialTheme.colorScheme.tertiary,
                        count = state.totalLife,
                        onClick = onNavigateToLife,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Distribución de Cartera", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                PortfolioDistributionCard(state.totalVehicles, state.totalLife)

                Text("Gestión de Reclamos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ModuleCard(
                        title = "Reclamos Veh.",
                        icon = Icons.Default.CarCrash,
                        color = MaterialTheme.colorScheme.error,
                        count = state.vehicleClaimsCount,
                        onClick = onNavigateToVehicleClaims,
                        modifier = Modifier.weight(1f)
                    )
                    ModuleCard(
                        title = "Reclamos Vida",
                        icon = Icons.Default.MedicalServices,
                        color = MaterialTheme.colorScheme.secondary,
                        count = state.lifeClaimsCount,
                        onClick = onNavigateToLifeClaims,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun FinancialCard(amount: Double) {

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text("Ingresos Totales", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
            Text(
                text = formatearMoneda(amount),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Generados este mes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun PortfolioDistributionCard(vehicles: Int, life: Int) {
    val total = vehicles + life
    val vehiclePercent = if (total > 0) vehicles.toFloat() / total else 0f
    val lifePercent = if (total > 0) life.toFloat() / total else 0f

    val vehicleColor = MaterialTheme.colorScheme.primary
    val lifeColor = MaterialTheme.colorScheme.tertiary

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                DonutChart(
                    proportions = listOf(vehiclePercent, lifePercent),
                    colors = listOf(vehicleColor, lifeColor)
                )
                Text("$total", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            }

            Spacer(modifier = Modifier.width(24.dp))

            Column {
                LegendItem("Vehículos", "$vehicles", vehicleColor)
                Spacer(modifier = Modifier.height(8.dp))
                LegendItem("Vida", "$life", lifeColor)
            }
        }
    }
}

@Composable
fun ModuleCard(
    title: String,
    icon: ImageVector,
    color: Color,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(140.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color)
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = color)
            }

            Column {
                Text(count.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = color.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
fun LegendItem(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(text = value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DonutChart(
    proportions: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val inactiveColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier = modifier) {
        var startAngle = -90f
        val strokeWidth = 30f

        proportions.forEachIndexed { index, proportion ->
            val sweepAngle = proportion * 360f
            drawArc(
                color = colors.getOrElse(index) { inactiveColor },
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(size.width - strokeWidth, size.height - strokeWidth),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )
            startAngle += sweepAngle
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AdminDashboardPreview() {
    val mockState = AdminUiState(
        totalRevenue = 1250000.0,
        totalPolicies = 45,
        totalVehicles = 30,
        totalLife = 15,
        vehicleClaimsCount = 5,
        lifeClaimsCount = 2,
        isLoading = false
    )

    AdminContent(
        state = mockState,
        onReload = {},
        onNavigateToVehicles = {},
        onNavigateToLife = {},
        onNavigateToVehicleClaims = {},
        onNavigateToLifeClaims = {},
        onLogout = {}
    )
}