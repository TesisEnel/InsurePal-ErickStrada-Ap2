package edu.ucne.InsurePal.presentation.utils

import java.text.NumberFormat
import java.util.Locale

fun formatearMoneda(cantidad: Double): String {
    return NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(cantidad)
}