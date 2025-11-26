package edu.ucne.InsurePal.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.ucne.InsurePal.ui.theme.InsurePalTheme

data class InsuranceOption(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val brandColor: Color? = null,
    val isEnabled: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionSeguroScreen(
    onNavigateBack: () -> Unit,
    onInsuranceSelected: (String) -> Unit
) {
    val options = listOf(
        InsuranceOption(
            id = "VEHICULO",
            title = "Seguro de Vehículo",
            description = "Protección completa para tu auto ante cualquier incidente.",
            icon = Icons.Default.DirectionsCar,
            brandColor = Color(0xFF4CAF50),
            isEnabled = true
        ),
        InsuranceOption(
            id = "HOGAR",
            title = "Seguro de Hogar",
            description = "Cuida tu vivienda contra robos, incendios y daños.",
            icon = Icons.Default.Home,
            isEnabled = false
        ),
        InsuranceOption(
            id = "SALUD",
            title = "Seguro Médico",
            description = "Atención médica de primera calidad para ti y tu familia.",
            icon = Icons.Default.MedicalServices,
            isEnabled = false
        ),
        InsuranceOption(
            id = "VIDA",
            title = "Seguro de Vida",
            description = "Asegura el futuro financiero de tus seres queridos.",
            icon = Icons.Default.Favorite,
            isEnabled = true
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Nuevo Seguro",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Selecciona el tipo de cobertura",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            items(options) { option ->
                InsuranceOptionCard(
                    option = option,
                    onClick = { onInsuranceSelected(option.id) }
                )
            }
        }
    }
}

@Composable
fun InsuranceOptionCard(
    option: InsuranceOption,
    onClick: () -> Unit
) {
    val containerColor = if (option.isEnabled) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainerLow
    val contentColor = if (option.isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)

    val iconTint = if (option.isEnabled) (option.brandColor ?: MaterialTheme.colorScheme.primary) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    val iconBackground = if (option.isEnabled) iconTint.copy(alpha = 0.1f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)

    Card(
        onClick = onClick,
        enabled = option.isEnabled,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBackground, MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    tint = iconTint
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = option.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (option.isEnabled) MaterialTheme.colorScheme.onSurfaceVariant else contentColor,
                    maxLines = 2
                )
            }


            if (option.isEnabled) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Badge(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    Text("Pronto", modifier = Modifier.padding(horizontal = 4.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SeleccionSeguroPreview() {
    InsurePalTheme {
        SeleccionSeguroScreen(onNavigateBack = {}, onInsuranceSelected = {})
    }
}