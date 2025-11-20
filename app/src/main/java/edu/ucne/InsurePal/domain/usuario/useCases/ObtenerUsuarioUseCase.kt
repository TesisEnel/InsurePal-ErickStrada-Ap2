package edu.ucne.InsurePal.domain.usuario.useCases

import edu.ucne.InsurePal.domain.usuario.repository.UsuarioRepository
import javax.inject.Inject

class ObtenerUsuarioUseCase @Inject constructor(private val repo : UsuarioRepository) {
    suspend operator fun invoke (id: Int?) = repo.getUsuario(id)
}