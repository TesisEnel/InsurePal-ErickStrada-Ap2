package edu.ucne.InsurePal.domain.polizas.vehiculo.useCases

import edu.ucne.InsurePal.domain.polizas.vehiculo.model.DesglosePrima
import jakarta.inject.Inject

class CalcularPrimaUseCase @Inject constructor() {

    operator fun invoke(valorMercado: Double, tipoCobertura: String): DesglosePrima {


        val tasaBase = when (tipoCobertura) {
            "Full Cobertura" -> 0.025
            "Daños a Terceros" -> 0.015
            "Ley" -> 0.010
            else -> 0.025
        }

        val primaNeta = valorMercado * tasaBase

        val tasaImpuesto = 0.18
        val impuestos = primaNeta * tasaImpuesto

        val total = primaNeta + impuestos

        return DesglosePrima(
            primaNeta = primaNeta,
            impuestos = impuestos,
            total = total
        )
    }
}