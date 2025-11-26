package edu.ucne.InsurePal.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.ucne.InsurePal.data.local.pago.PagoDao
import edu.ucne.InsurePal.data.local.pago.PagoEntity

@Database(
    entities = [
        PagoEntity::class,
    ],
    version = 1,
    exportSchema = false
)

abstract class PagoDb: RoomDatabase(){
    abstract fun PagoDao(): PagoDao
}