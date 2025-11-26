package edu.ucne.InsurePal.data.remote.polizas.vida

data class SeguroVidaRequest(
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
    val prima: Double
)