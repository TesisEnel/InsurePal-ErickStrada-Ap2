package edu.ucne.InsurePal.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import edu.ucne.InsurePal.data.local.pago.PagoDao
import edu.ucne.InsurePal.data.database.PagoDb


@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun providesAppDb(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(
            appContext,
            PagoDb::class.java,
            "JugadorDb"
        ).fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providesPagoDao(appDb: PagoDb) = appDb.PagoDao()
}

