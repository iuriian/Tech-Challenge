package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.AtualizarFuncionarioRequest
import br.com.fiap.oficina.application.dto.CriarFuncionarioRequest
import br.com.fiap.oficina.application.dto.FuncionarioResponse
import br.com.fiap.oficina.domain.entity.Funcionario
import org.springframework.stereotype.Component

@Component
class FuncionarioApplicationMapper {
    fun toDomain(request: CriarFuncionarioRequest): Funcionario = Funcionario.criar(
        nome = request.nome,
        cargo = request.cargo,
    )

    fun toDomain(id: String, request: AtualizarFuncionarioRequest): Funcionario = Funcionario.reconstruir(
        id = id,
        nome = request.nome,
        cargo = request.cargo,
    )

    fun toResponse(funcionario: Funcionario): FuncionarioResponse = FuncionarioResponse(
        id = funcionario.id.valor.toString(),
        nome = funcionario.nome,
        cargo = funcionario.cargo.name,
    )
}
