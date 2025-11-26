package edu.ucne.InsurePal.data.local.pago

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pagos")
data class PagoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val polizaId: String,
    val usuarioId: Int,
    val fechaIso: String,
    val monto: Double,
    val tarjetaMascara: String,
    val estado: String,
    val numeroConfirmacion: String
)