package edu.ucne.InsurePal.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.InsurePal.ui.theme.InsurePalTheme


data class Policy(
    val name: String,
    val id: String,
    val status: String,
    val icon: ImageVector,
    val color: Color
)

data class QuickAction(
    val title: String,
    val icon: ImageVector
)


@Composable
fun InsuranceHomeScreen(
    onActionClick: (String) -> Unit
) {
    val policies = listOf(
        Policy("Seguro de Auto", "POL-8821", "Activo", Icons.Default.DirectionsCar, Color(0xFF4CAF50)),
        Policy("Seguro de Hogar", "POL-1102", "Pago Pendiente", Icons.Default.Home, Color(0xFF2196F3)),
        Policy("Gastos Médicos", "POL-3341", "Activo", Icons.Default.MedicalServices, Color(0xFFE91E63))
    )

    val actions = listOf(
        QuickAction("Reportar Siniestro", Icons.Default.Warning),
        QuickAction("Pedir Asistencia", Icons.Default.SupportAgent),
        QuickAction("Mis Pagos", Icons.Default.CreditCard),
        QuickAction("Nuevo Seguro", Icons.Default.AddCircle)
    )

    Scaffold(
        topBar = { HomeHeader() },
        containerColor = Color(0xFFF5F7FA)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "¿Qué deseas proteger hoy?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
            }

            item {
                SectionTitle("Mis Pólizas")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(policies) { policy ->
                        PolicyCard(policy)
                    }
                }
            }
            item {
                SectionTitle("Acciones Rápidas")
                QuickActionsGrid(actions,
                    onItemClick = onActionClick)
            }

            item {
                PromoBanner()
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}


@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Bienvenido de nuevo",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
        IconButton(
            onClick = { /* TODO: Abrir notificaciones */ },
            modifier = Modifier
                .background(Color.White, CircleShape)
                .size(48.dp)
        ) {
            Icon(imageVector = Icons.Outlined.Notifications, contentDescription = "Alertas")
        }
    }
}

@Composable
fun PolicyCard(policy: Policy) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(280.dp)
            .height(160.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .background(policy.color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Icon(imageVector = policy.icon, contentDescription = null, tint = policy.color)
                }

                SuggestionChip(
                    onClick = {},
                    label = { Text(policy.status, fontSize = 10.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if(policy.status == "Activo") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                        labelColor = Color.Black
                    ),
                    border = null
                )
            }
            Column {
                Text(text = policy.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = policy.id, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun QuickActionsGrid(
    actions: List<QuickAction>,
    onItemClick: (String) -> Unit
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ActionItem(actions[0], Modifier.weight(1f), onItemClick)
            ActionItem(actions[1], Modifier.weight(1f), onItemClick)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ActionItem(actions[2], Modifier.weight(1f), onItemClick)
            ActionItem(actions[3], Modifier.weight(1f), onItemClick)
        }
    }
}

@Composable
fun ActionItem(
    action: QuickAction,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    Button(
        onClick = { onClick(action.title) },
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = action.icon, contentDescription = null, tint = Color(0xFF3F51B5))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = action.title, color = Color.Black, fontSize = 12.sp, maxLines = 1)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
    )
}

@Composable
fun PromoBanner() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF212121)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Asegura tu mascota", color = Color.White, fontWeight = FontWeight.Bold)
                Text("15% OFF este mes", color = Color(0xFFFFD700), style = MaterialTheme.typography.bodyMedium)
            }
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Modo Claro"
)
@Composable
fun InsuranceHomeScreenPreview() {
    InsurePalTheme {
        InsuranceHomeScreen(onActionClick = {})
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    name = "Modo Oscuro"
)
@Composable
fun InsuranceHomeScreenDarkPreview() {
    MaterialTheme {
        InsuranceHomeScreen(onActionClick = {})
    }
}