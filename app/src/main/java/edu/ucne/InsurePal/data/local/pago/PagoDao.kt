package edu.ucne.InsurePal.data.local.pago

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PagoDao {
    @Query("SELECT * FROM pagos WHERE usuarioId = :userId ORDER BY fechaIso DESC")
    fun getPagosPorUsuario(userId: Int): Flow<List<PagoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPago(pago: PagoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pagos: List<PagoEntity>)
}