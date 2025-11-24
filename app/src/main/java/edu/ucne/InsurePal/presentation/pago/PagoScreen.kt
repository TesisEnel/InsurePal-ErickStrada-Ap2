package edu.ucne.InsurePal.presentation.pago

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.InsurePal.presentation.pago.formateo.CreditCardFilter
import edu.ucne.InsurePal.presentation.pago.formateo.DateFilter
import edu.ucne.InsurePal.presentation.pago.formateo.formatearMoneda
import edu.ucne.InsurePal.ui.theme.InsurePalTheme
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagoScreen(
    viewModel: PagoViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()


    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onPaymentSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Realizar Pago") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                PagoContent(
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }
        }

        if (state.mensajeError != null) {
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(PagoEvent.OnDialogDismiss) },
                icon = { Icon(Icons.Default.Lock, contentDescription = null) },
                title = { Text("Error en el Pago") },
                text = { Text(state.mensajeError ?: "Ocurrió un error inesperado") },
                confirmButton = {
                    TextButton(onClick = { viewModel.onEvent(PagoEvent.OnDialogDismiss) }) {
                        Text("Entendido")
                    }
                }
            )
        }
    }
}

@Composable
fun PagoContent(
    state: PagoUiState,
    onEvent: (PagoEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        TarjetaCreditoVisual(
            numero = state.numeroTarjeta,
            titular = state.titular,
            fecha = state.fechaVencimiento
        )

        Text(
            text = "Total a Pagar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "RD$ ${formatearMoneda(state.montoAPagar)}",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = state.numeroTarjeta,
            onValueChange = { if (it.length <= 16) onEvent(PagoEvent.OnNumeroChange(it)) },
            label = { Text("Número de Tarjeta") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            visualTransformation = CreditCardFilter
        )

        OutlinedTextField(
            value = state.titular,
            onValueChange = { onEvent(PagoEvent.OnTitularChange(it)) },
            label = { Text("Titular de la Tarjeta") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.fechaVencimiento,
                onValueChange = { if (it.length <= 4) onEvent(PagoEvent.OnFechaChange(it)) },
                label = { Text("Vence (MM/YY)") },
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                visualTransformation = DateFilter
            )

            OutlinedTextField(
                value = state.cvv,
                onValueChange = { if (it.length <= 4) onEvent(PagoEvent.OnCvvChange(it)) },
                label = { Text("CVV") },
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onEvent(PagoEvent.OnPagarClick) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pagar Ahora")
        }

        Text(
            text = "Sus datos están protegidos",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}


@Composable
fun TarjetaCreditoVisual(numero: String, titular: String, fecha: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF1E1E1E), Color(0xFF424242)) // Efecto Dark Premium
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp, 30.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                )

                Text(
                    text = if (numero.isNotEmpty()) numero.chunked(4).joinToString(" ") else "**** **** **** ****",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    letterSpacing = 2.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("TITULAR", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = titular.ifEmpty { "NOMBRE APELLIDO" }.uppercase(),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Column {
                        Text("VENCE", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = if (fecha.isNotEmpty() && fecha.length >= 2)
                                "${fecha.take(2)}/${fecha.drop(2)}"
                            else "MM/YY",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}








@Preview(showSystemUi = true)
@Composable
fun PagoScreenPreview() {
    InsurePalTheme {
        PagoContent(
            state = PagoUiState(
                montoAPagar = 5400.0,
                numeroTarjeta = "4242424242424242",
                titular = "JUAN PEREZ",
                fechaVencimiento = "1225"
            ),
            onEvent = {}
        )
    }
}