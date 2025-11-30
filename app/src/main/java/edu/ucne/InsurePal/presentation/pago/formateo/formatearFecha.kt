package edu.ucne.InsurePal.presentation.pago.formateo

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun formatearFecha(fecha: String): String {
    return try {
        if (fecha.length < 10 || !fecha.contains("-")) return fecha

        val fechaLimpia = fecha.substringBefore("T").substringBefore(" ")

        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val date = LocalDate.parse(fechaLimpia, inputFormatter)
        date.format(outputFormatter)
    } catch (e: Exception) {
        if (fecha.length > 10) fecha.take(10) else fecha
    }
}