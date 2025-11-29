package edu.ucne.InsurePal.data

import edu.ucne.InsurePal.data.local.pago.PagoEntity
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.MarcaVehiculoDto
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.ModeloVehiculoDto
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.SeguroVehiculoRequest
import edu.ucne.InsurePal.data.remote.polizas.vehiculo.dto.SeguroVehiculoResponse
import edu.ucne.InsurePal.data.remote.polizas.vida.SeguroVidaRequest
import edu.ucne.InsurePal.data.remote.polizas.vida.SeguroVidaResponse
import edu.ucne.InsurePal.data.remote.reclamoVehiculo.ReclamoResponse
import edu.ucne.InsurePal.data.remote.reclamoVida.dto.ReclamoVidaResponse
import edu.ucne.InsurePal.data.remote.usuario.dto.UsuarioRequest
import edu.ucne.InsurePal.data.remote.usuario.dto.UsuarioResponse
import edu.ucne.InsurePal.domain.pago.model.EstadoPago
import edu.ucne.InsurePal.domain.pago.model.Pago
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.MarcaVehiculo
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.ModeloVehiculo
import edu.ucne.InsurePal.domain.polizas.vehiculo.model.SeguroVehiculo
import edu.ucne.InsurePal.domain.polizas.vida.model.SeguroVida
import edu.ucne.InsurePal.domain.reclamoVehiculo.model.ReclamoVehiculo
import edu.ucne.InsurePal.domain.reclamoVida.model.ReclamoVida
import edu.ucne.InsurePal.domain.usuario.model.Usuario
import java.time.LocalDateTime


fun Usuario.toRequest(): UsuarioRequest = UsuarioRequest(
    userName = userName,
    password = password
)

fun UsuarioResponse.toDomain() = Usuario(
    usuarioId = usuarioId,
    userName = userName,
    password = password
)

fun SeguroVehiculoResponse.toDomain() = SeguroVehiculo(
    usuarioId = usuarioId,
    idPoliza = idPoliza,
    name = name,
    marca = marca,
    modelo = modelo,
    anio = anio,
    color = color,
    placa = placa,
    chasis = chasis,
    valorMercado = valorMercado,
    coverageType = coverageType,
    status = status,
    expirationDate = expirationDate,
    esPagado = esPagado,
    fechaPago = fechaPago
)

fun SeguroVehiculo.toRequest(): SeguroVehiculoRequest = SeguroVehiculoRequest(
    usuarioId = usuarioId,
    name = name,
    marca = marca,
    modelo = modelo,
    anio = anio,
    color = color,
    placa = placa,
    chasis = chasis,
    valorMercado = valorMercado,
    coverageType = coverageType,
    status = status,
    expirationDate = expirationDate,
    esPagado = esPagado,
    fechaPago = fechaPago
)

 fun PagoEntity.toDomain(): Pago {
    return Pago(
        id = id,
        polizaId = polizaId,
        usuarioId = usuarioId,
        monto = monto,
        fecha = try {
            LocalDateTime.parse(fechaIso)
        } catch (e: Exception) {
            LocalDateTime.now()
        },
        estado = if (estado.equals("APROBADO", ignoreCase = true)) EstadoPago.APROBADO else EstadoPago.RECHAZADO,
        tarjetaUltimosDigitos = tarjetaMascara,

        numeroConfirmacion = numeroConfirmacion
    )
}

 fun SeguroVidaResponse.toDomain() = SeguroVida(
     id = id,
     usuarioId = usuarioId,
     nombresAsegurado = nombresAsegurado,
     cedulaAsegurado = cedulaAsegurado,
     fechaNacimiento = fechaNacimiento,
     ocupacion = ocupacion,
     esFumador = esFumador,
     nombreBeneficiario = nombreBeneficiario,
     cedulaBeneficiario = cedulaBeneficiario,
     parentesco = parentesco,
     montoCobertura = montoCobertura,
     prima = prima,
     esPagado = esPagado,
     fechaPago = fechaPago
 )

 fun SeguroVida.toRequest() = SeguroVidaRequest(
    usuarioId = usuarioId,
    nombresAsegurado = nombresAsegurado,
    cedulaAsegurado = cedulaAsegurado,
    fechaNacimiento = fechaNacimiento,
    ocupacion = ocupacion,
    esFumador = esFumador,
    nombreBeneficiario = nombreBeneficiario,
    cedulaBeneficiario = cedulaBeneficiario,
    parentesco = parentesco,
    montoCobertura = montoCobertura,
    prima = prima
)

fun MarcaVehiculoDto.toDomain() = MarcaVehiculo(
    nombre = nombre,
    modelos = modelos.map { it.toDomain() }
)

fun ModeloVehiculoDto.toDomain() = ModeloVehiculo(
    nombre = nombre,
    precioBase = precioBase
)

fun ReclamoResponse.toDomain(): ReclamoVehiculo {
    return ReclamoVehiculo(
        id = this.id,
        folio = this.folio,
        polizaId = this.polizaId,
        usuarioId = this.usuarioId,
        descripcion = this.descripcion,
        direccion = this.direccion,
        tipoIncidente = this.tipoIncidente,
        fechaIncidente = this.fechaIncidente,
        imagenUrl = this.imagenUrl,
        status = this.status,
        motivoRechazo = this.motivoRechazo,
        fechaCreacion = this.fechaCreacion,
        numCuenta = numCuenta
    )
}


fun List<ReclamoResponse>.toDomain(): List<ReclamoVehiculo> {
    return this.map { it.toDomain() }
}

fun ReclamoVidaResponse.toDomain(): ReclamoVida {
    return ReclamoVida(
        id = id,
        folio = folio,
        polizaId = polizaId,
        usuarioId = usuarioId,
        nombreAsegurado = nombreAsegurado,
        descripcion = descripcion,
        lugarFallecimiento = lugarFallecimiento,
        causaMuerte = causaMuerte,
        fechaFallecimiento = fechaFallecimiento,

        numCuenta = numCuenta ?: "",

        actaDefuncionUrl = actaDefuncionUrl,
        identificacionUrl = identificacionUrl,

        status = status,
        motivoRechazo = motivoRechazo,
        fechaCreacion = fechaCreacion
    )
}

fun List<ReclamoVidaResponse>.toDomain(): List<ReclamoVida> {
    return this.map { it.toDomain() }
}