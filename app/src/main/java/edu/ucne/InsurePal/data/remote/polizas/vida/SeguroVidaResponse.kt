package edu.ucne.InsurePal.data.remote.polizas.vida

data class SeguroVidaResponse(
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
    val esPagado: Boolean,
    val fechaPago: String?
)