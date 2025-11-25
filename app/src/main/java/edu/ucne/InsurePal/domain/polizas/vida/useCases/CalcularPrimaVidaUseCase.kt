package edu.ucne.InsurePal.domain.polizas.vida.useCases


import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CalcularPrimaVidaUseCase @Inject constructor() {

    operator fun invoke(
        fechaNacimiento: String,
        esFumador: Boolean,
        ocupacion: String,
        montoCobertura: Double
    ): Double {


        val edad = calcularEdad(fechaNacimiento)
        if (edad < 18) return 0.0 // No aseguramos menores

        var tasa = 0.005

        if (edad > 30) tasa += 0.002
        if (edad > 45) tasa += 0.005
        if (edad > 60) tasa += 0.015

        if (esFumador) {
            tasa *= 1.30
        }

        when (ocupacion.lowercase()) {
            "bombero", "policia", "militar", "construccion" -> tasa *= 1.50
            "chofer", "mecánico" -> tasa *= 1.20
            else -> { /* Oficinista, Estudiante, etc. mantiene tasa igual */ }
        }

        val primaAnual = montoCobertura * tasa

        return primaAnual
    }

    private fun calcularEdad(fechaString: String): Int {
        return try {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val nacimiento = LocalDate.parse(fechaString.take(10), formatter)
            val hoy = LocalDate.now()
            Period.between(nacimiento, hoy).years
        } catch (e: Exception) {
            0
        }
    }
}