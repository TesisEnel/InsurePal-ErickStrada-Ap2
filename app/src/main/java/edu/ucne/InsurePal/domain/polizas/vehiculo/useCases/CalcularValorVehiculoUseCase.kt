package edu.ucne.InsurePal.domain.polizas.vehiculo.useCases

import edu.ucne.InsurePal.domain.polizas.vehiculo.repository.VehiculoRepository
import jakarta.inject.Inject
import java.util.Calendar

class CalcularValorVehiculoUseCase @Inject constructor(
    private val repository: VehiculoRepository
) {
    operator fun invoke(marca: String, modelo: String, anio: String): Double {

        val anioVehiculo = anio.toIntOrNull() ?: return 0.0

        if (modelo.isBlank()) return 0.0

        val precioBase = repository.getPrecioBase(marca, modelo) ?: return 0.0

        val anioActual = Calendar.getInstance().get(Calendar.YEAR)

        if (anioVehiculo > anioActual) return precioBase

        val antiguedad = anioActual - anioVehiculo
        val porcentajeDepreciacion = antiguedad * 0.05
        val valorDescontado = precioBase * (1 - porcentajeDepreciacion)

        val valorMinimo = precioBase * 0.20

        return maxOf(valorDescontado, valorMinimo)
    }
}