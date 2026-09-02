package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.FuncionarioRequest
import br.com.fiap.oficina.application.dto.FuncionarioResponse
import br.com.fiap.oficina.domain.entity.Funcionario
import org.springframework.stereotype.Component

@Component
class FuncionarioMapper {
    fun toDomain(request: FuncionarioRequest): Funcionario = if (request.id == null) {
        Funcionario.criar(
            nome = request.nome,
            cargo = request.cargo,
        )
    } else {
        Funcionario.reconstruir(
            id = request.id,
            nome = request.nome,
            cargo = request.cargo,
        )
    }

    fun toResponse(funcionario: Funcionario): FuncionarioResponse = FuncionarioResponse(
        id = funcionario.id.valor.toString(),
        nome = funcionario.nome,
        cargo = funcionario.cargo.name,
    )
}
