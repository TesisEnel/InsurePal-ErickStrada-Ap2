package edu.ucne.InsurePal.presentation.polizas.vida


data class SeguroVidaUiState(
    val isLoading: Boolean = false,
    val errorGlobal: String? = null,
    val isSuccess: Boolean = false,
    val cotizacionIdCreada: String? = null,

    val nombres: String = "",
    val errorNombres: String? = null,

    val cedula: String = "",
    val errorCedula: String? = null,

    val fechaNacimiento: String = "",
    val errorFechaNacimiento: String? = null,

    val ocupacion: String = "",
    val errorOcupacion: String? = null,

    val esFumador: Boolean = false,

    val nombreBeneficiario: String = "",
    val errorNombreBeneficiario: String? = null,

    val cedulaBeneficiario: String = "",
    val errorCedulaBeneficiario: String? = null,

    val parentesco: String = "",
    val errorParentesco: String? = null,

    val montoCobertura: String = "",
    val errorMontoCobertura: String? = null,

    val primaCalculada: Double = 0.0
)