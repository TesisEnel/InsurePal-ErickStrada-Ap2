package edu.ucne.InsurePal.domain.polizas.vida.useCases

import edu.ucne.InsurePal.domain.polizas.vida.model.SeguroVidaValidationResult
import edu.ucne.InsurePal.domain.polizas.vida.model.ValidateSeguroVidaParams
import jakarta.inject.Inject

class ValidateSeguroVidaUseCase @Inject constructor() {

    operator fun invoke(params: ValidateSeguroVidaParams): SeguroVidaValidationResult {

        return with(params) {

            val montoVal = montoCobertura.toDoubleOrNull() ?: 0.0

            SeguroVidaValidationResult(
                errorNombres = if (nombres.isBlank()) "Requerido" else null,

                errorCedula = if (cedula.length != 11 || !cedula.all { it.isDigit() })
                    "Debe tener 11 dígitos" else null,

                errorFechaNacimiento = if (fechaNacimiento.isBlank()) "Requerida" else null,

                errorOcupacion = if (ocupacion.isBlank()) "Seleccione una ocupación" else null,

                errorMontoCobertura = when {
                    montoVal <= 0 -> "Monto inválido"
                    montoVal > 1_000_000 -> "Máximo RD$ 1,000,000"
                    else -> null
                },

                errorNombreBeneficiario = if (nombreBeneficiario.isBlank()) "Requerido" else null,

                errorCedulaBeneficiario = if (cedulaBeneficiario.length != 11 || !cedulaBeneficiario.all { it.isDigit() })
                    "Debe tener 11 dígitos" else null,

                errorParentesco = if (parentesco.isBlank()) "Requerido" else null
            )
        }
    }
}