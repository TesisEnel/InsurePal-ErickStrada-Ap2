package edu.ucne.InsurePal.presentation.pago.formateo

import java.text.NumberFormat
import java.util.Locale

fun formatearMoneda(cantidad: Double): String {
    return NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(cantidad)
}