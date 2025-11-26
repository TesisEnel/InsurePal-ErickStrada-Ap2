package edu.ucne.InsurePal.data.remote.polizas.vehiculo

import edu.ucne.InsurePal.domain.polizas.vehiculo.model.MarcaVehiculo
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.ModeloVehiculo
import edu.ucne.InsurePal.domain.polizas.vehiculo.repository.VehiculoRepository
import jakarta.inject.Inject

class VehiculoRepositoryImpl @Inject constructor() : VehiculoRepository {

    private val catalogoVehiculos = listOf(
        MarcaVehiculo(
            nombre = "Toyota",
            modelos = listOf(
                ModeloVehiculo("Corolla", 1200000.0),
                ModeloVehiculo("Camry", 1800000.0),
                ModeloVehiculo("Rav4", 2100000.0),
                ModeloVehiculo("Hilux", 2600000.0)
            )
        ),
        MarcaVehiculo(
            nombre = "Honda",
            modelos = listOf(
                ModeloVehiculo("Civic", 1300000.0),
                ModeloVehiculo("CR-V", 1950000.0),
                ModeloVehiculo("Pilot", 2400000.0),
                ModeloVehiculo("HR-V", 1600000.0)
            )
        ),
        MarcaVehiculo(
            nombre = "Kia",
            modelos = listOf(
                ModeloVehiculo("Picanto", 750000.0),
                ModeloVehiculo("Rio", 950000.0),
                ModeloVehiculo("Sportage", 1700000.0),
                ModeloVehiculo("Sorento", 2300000.0)
            )
        ),
        MarcaVehiculo(
            nombre = "Hyundai",
            modelos = listOf(
                ModeloVehiculo("Grand i10", 780000.0),
                ModeloVehiculo("Elantra", 1100000.0),
                ModeloVehiculo("Tucson", 1750000.0),
                ModeloVehiculo("Santa Fe", 2250000.0)
            )
        ),
        MarcaVehiculo(
            nombre = "Nissan",
            modelos = listOf(
                ModeloVehiculo("Versa", 980000.0),
                ModeloVehiculo("Sentra", 1250000.0),
                ModeloVehiculo("Kicks", 1450000.0),
                ModeloVehiculo("X-Trail", 1850000.0)
            )
        ),
        MarcaVehiculo(
            nombre = "Chevrolet",
            modelos = listOf(
                ModeloVehiculo("Spark", 600000.0),
                ModeloVehiculo("Onix", 900000.0),
                ModeloVehiculo("Trax", 1300000.0),
                ModeloVehiculo("Tahoe", 3800000.0)
            )
        )
    )

    override fun getMarcas(): List<MarcaVehiculo> {
        return catalogoVehiculos
    }

    override fun getPrecioBase(marca: String, modelo: String): Double? {
        return catalogoVehiculos
            .find { it.nombre.equals(marca, ignoreCase = true) }
            ?.modelos
            ?.find { it.nombre.equals(modelo, ignoreCase = true) }
            ?.precioBase
    }
}