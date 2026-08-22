package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.presentation.dto.FuncionarioDto
import org.springframework.stereotype.Component

@Component
class FuncionarioMapper {
    fun toResponse(funcionario: Funcionario): FuncionarioDto =
        FuncionarioDto(
            id = funcionario.id.valor.toString(),
            nome = funcionario.nome,
            cargo = funcionario.cargo.name,
        )

    fun toEntity(dto: FuncionarioDto): Funcionario =
        Funcionario.criar(
            nome = dto.nome,
            cargo = dto.cargo,
        )

    fun toEntityComId(
        id: String,
        dto: FuncionarioDto,
    ): Funcionario =
        Funcionario.reconstruir(
            id = id,
            nome = dto.nome,
            cargo = dto.cargo,
        )
}
