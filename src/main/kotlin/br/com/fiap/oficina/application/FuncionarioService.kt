package br.com.fiap.oficina.application

import br.com.fiap.oficina.application.mapper.toDto
import br.com.fiap.oficina.domain.model.Funcionario
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.adapter.FuncionarioRepositoryAdapter
import br.com.fiap.oficina.presentation.dto.FuncionarioDto
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class FuncionarioService(
    private val repositoryAdapter: FuncionarioRepositoryAdapter,
) {
    fun cadastrar(dto: FuncionarioDto) {
        val funcionario =
            Funcionario
                .criar(
                    nome = dto.nome,
                    cargo = dto.cargo,
                )

        repositoryAdapter.salvar(funcionario)
    }

    fun listarTodos(): List<FuncionarioDto> = repositoryAdapter.listarTodos().map { it.toDto() }

    fun buscarPorId(id: UUID): FuncionarioDto? {
        val resultado = repositoryAdapter.buscarPorId(Id(id))

        return resultado?.toDto()
    }

    fun editar(funcionario: FuncionarioDto) = repositoryAdapter

    fun deletar() {}
}
