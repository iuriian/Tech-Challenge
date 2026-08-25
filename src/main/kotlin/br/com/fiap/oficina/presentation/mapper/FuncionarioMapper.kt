package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.application.dto.AtualizarFuncionarioRequest
import br.com.fiap.oficina.application.dto.CriarFuncionarioRequest
import br.com.fiap.oficina.application.dto.FuncionarioResponse
import br.com.fiap.oficina.presentation.dto.FuncionarioDto
import org.springframework.stereotype.Component

@Component
class FuncionarioMapper {
    fun toCriarRequest(dto: FuncionarioDto): CriarFuncionarioRequest = CriarFuncionarioRequest(
        nome = dto.nome,
        cargo = dto.cargo,
    )

    fun toAtualizarRequest(dto: FuncionarioDto): AtualizarFuncionarioRequest = AtualizarFuncionarioRequest(
        nome = dto.nome,
        cargo = dto.cargo,
    )

    fun toDto(response: FuncionarioResponse): FuncionarioDto = FuncionarioDto(
        id = response.id,
        nome = response.nome,
        cargo = response.cargo,
    )
}
