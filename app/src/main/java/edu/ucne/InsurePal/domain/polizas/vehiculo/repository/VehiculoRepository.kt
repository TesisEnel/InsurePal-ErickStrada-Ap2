package edu.ucne.InsurePal.domain.polizas.vehiculo.repository

import edu.ucne.InsurePal.domain.polizas.vehiculo.model.MarcaVehiculo

interface VehiculoRepository {
    fun getMarcas(): List<MarcaVehiculo>
    fun getPrecioBase(marca: String, modelo: String): Double?
}