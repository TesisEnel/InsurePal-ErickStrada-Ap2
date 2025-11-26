package edu.ucne.InsurePal.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.ucne.InsurePal.data.local.pago.PagoRepositoryImpl
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.SeguroVehiculoRepositoryImpl
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.VehiculoRepositoryImpl
import edu.ucne.InsurePal.data.remote.polizas.vida.SeguroVidaRepositoryImpl
import edu.ucne.InsurePal.data.remote.usuario.UsuarioRepositoryImpl
import edu.ucne.InsurePal.domain.pago.repository.PagoRepository
import edu.ucne.InsurePal.domain.polizas.vehiculo.repository.SeguroVehiculoRepository
import edu.ucne.InsurePal.domain.polizas.vehiculo.repository.VehiculoRepository
import edu.ucne.InsurePal.domain.polizas.vida.repository.SeguroVidaRepository
import edu.ucne.InsurePal.domain.usuario.repository.UsuarioRepository
import javax.inject.Singleton

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

    @Binds
    @Singleton
    abstract fun bindPagoRepository(
        pagoRepositoryImpl: PagoRepositoryImpl
    ): PagoRepository

    @Binds
    @Singleton
    abstract fun bindSeguroVidaRepository(
        impl: SeguroVidaRepositoryImpl
    ): SeguroVidaRepository
}