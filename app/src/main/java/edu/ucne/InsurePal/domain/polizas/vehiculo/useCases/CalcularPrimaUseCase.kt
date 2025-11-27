package edu.ucne.InsurePal.domain.polizas.vehiculo.useCases

import edu.ucne.InsurePal.domain.polizas.vehiculo.model.DesglosePrima
import jakarta.inject.Inject

class CalcularPrimaUseCase @Inject constructor() {

    operator fun invoke(valorMercado: Double, tipoCobertura: String): DesglosePrima {
        val tasaBaseAnual = when (tipoCobertura) {
            "Full Cobertura" -> 0.025   // 2.5% anual
            "Daños a Terceros" -> 0.015 // 1.5% anual
            "Ley" -> 0.010              // 1.0% anual
            else -> 0.025
        }

        val primaNetaAnual = valorMercado * tasaBaseAnual

        val primaNetaMensual = primaNetaAnual / 12

        val tasaImpuesto = 0.18
        val impuestosMensual = primaNetaMensual * tasaImpuesto

        val totalMensual = primaNetaMensual + impuestosMensual

        return DesglosePrima(
            primaNeta = primaNetaMensual,
            impuestos = impuestosMensual,
            total = totalMensual
        )
    }
}