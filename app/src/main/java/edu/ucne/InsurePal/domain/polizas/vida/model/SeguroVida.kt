package edu.ucne.InsurePal.domain.polizas.vida.model

data class SeguroVida(
    val id: String,
    val usuarioId: Int,

    val nombresAsegurado: String,
    val cedulaAsegurado: String,
    val fechaNacimiento: String,
    val ocupacion: String,
    val esFumador: Boolean,

    val nombreBeneficiario: String,
    val cedulaBeneficiario: String,
    val parentesco: String,

    val montoCobertura: Double,
    val prima: Double,
    val esPagado: Boolean = false,
    val fechaPago: String? = null
)