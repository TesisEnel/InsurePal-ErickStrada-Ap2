package edu.ucne.InsurePal.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.InsurePal.data.local.UserPreferences
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.SeguroVehiculoRepositoryImpl
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.VehiculoRepositoryImpl
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.api.SeguroVehiculoApiService
import edu.ucne.InsurePal.data.remote.usuario.api.UsuarioApiService
import edu.ucne.InsurePal.data.remote.usuario.UsuarioRepositoryImpl
import edu.ucne.InsurePal.domain.polizas.vehiculo.repository.SeguroVehiculoRepository
import edu.ucne.InsurePal.domain.polizas.vehiculo.repository.VehiculoRepository
import edu.ucne.InsurePal.domain.usuario.repository.UsuarioRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object Module {
    const val BASE_URL = "https://insurepal.azurewebsites.net/"

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }


    @Provides
    @Singleton
    fun provideUsuarioApiService(moshi: Moshi): UsuarioApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(UsuarioApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideVehiculoApiService(moshi: Moshi): SeguroVehiculoApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SeguroVehiculoApiService::class.java)
    }

    @Module
    @InstallIn(SingletonComponent::class)

    abstract class RepositoryModule {
        @Binds
        @Singleton
        abstract fun bindUsuarioRepository(
            impl: UsuarioRepositoryImpl
        ): UsuarioRepository
        @Binds
        @Singleton
        abstract fun bindSeguroVehiculoRepository(
            impl: SeguroVehiculoRepositoryImpl
        ): SeguroVehiculoRepository

        @Binds
        abstract fun bindVehiculoRepository(
            impl: VehiculoRepositoryImpl
        ): VehiculoRepository
    }}