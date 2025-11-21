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
    val color: Color // Mantenemos esto si quieres colores específicos de marca por tipo de seguro, o lo cambiamos a null para usar Theme
)

data class QuickAction(
    val title: String,
    val icon: ImageVector
)

@Composable
fun InsuranceHomeScreen(
    onActionClick: (String) -> Unit
) {
    // Nota: Usamos los colores del tema para los iconos de las pólizas para mantener consistencia
    val policies = listOf(
        Policy("Seguro de Auto", "POL-8821", "Activo", Icons.Default.DirectionsCar, MaterialTheme.colorScheme.primary),
        Policy("Seguro de Hogar", "POL-1102", "Pago Pendiente", Icons.Default.Home, MaterialTheme.colorScheme.secondary),
        Policy("Gastos Médicos", "POL-3341", "Activo", Icons.Default.MedicalServices, MaterialTheme.colorScheme.tertiary)
    )

    val actions = listOf(
        QuickAction("Reportar Siniestro", Icons.Default.Warning),
        QuickAction("Pedir Asistencia", Icons.Default.SupportAgent),
        QuickAction("Mis Pagos", Icons.Default.CreditCard),
        QuickAction("Nuevo Seguro", Icons.Default.AddCircle)
    )

    Scaffold(
        topBar = { HomeHeader() },
        containerColor = MaterialTheme.colorScheme.background // COLOR DINÁMICO DEL TEMA
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
                    color = MaterialTheme.colorScheme.onBackground // COLOR DINÁMICO
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
                QuickActionsGrid(
                    actions = actions,
                    onItemClick = onActionClick
                )
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
                color = MaterialTheme.colorScheme.onSurface, // COLOR DINÁMICO
                fontWeight = FontWeight.SemiBold
            )
        }
        IconButton(
            onClick = { /* TODO: Abrir notificaciones */ },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape) // COLOR DINÁMICO
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Alertas",
                tint = MaterialTheme.colorScheme.onSurface // COLOR DINÁMICO
            )
        }
    }
}

@Composable
fun PolicyCard(policy: Policy) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer // COLOR DINÁMICO (Tarjeta elevada)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                // Icono de la póliza
                Box(
                    modifier = Modifier
                        .background(policy.color.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Icon(
                        imageVector = policy.icon,
                        contentDescription = null,
                        tint = policy.color
                    )
                }

                // Chip de estado dinámico
                val chipContainerColor = if (policy.status == "Activo")
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer

                val chipContentColor = if (policy.status == "Activo")
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onErrorContainer

                SuggestionChip(
                    onClick = {},
                    label = { Text(policy.status, fontSize = 10.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = chipContainerColor,
                        labelColor = chipContentColor
                    ),
                    border = null,
                    shape = CircleShape
                )
            }
            Column {
                Text(
                    text = policy.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface // COLOR DINÁMICO
                )
                Text(
                    text = policy.id,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // COLOR DINÁMICO (Gris del tema)
                )
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
        shape = MaterialTheme.shapes.medium, // Usa la forma definida en el tema (Theme.kt)
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh, // COLOR DINÁMICO
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary // COLOR DINÁMICO
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = action.title,
                color = MaterialTheme.colorScheme.onSurface, // COLOR DINÁMICO
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface, // COLOR DINÁMICO
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
    )
}

@Composable
fun PromoBanner() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.inverseSurface // Fondo oscuro del tema
        ),
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
                Text(
                    text = "Asegura tu mascota",
                    color = MaterialTheme.colorScheme.inverseOnSurface, // Texto claro sobre fondo oscuro
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "15% OFF este mes",
                    color = MaterialTheme.colorScheme.tertiaryContainer, // Color de acento
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.inverseOnSurface, // Icono claro
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