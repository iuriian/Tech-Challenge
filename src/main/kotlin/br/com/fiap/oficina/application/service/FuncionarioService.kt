package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.dto.FuncionarioDto
import br.com.fiap.oficina.presentation.mapper.toDto
import org.springframework.stereotype.Service

@Service
class FuncionarioService(private val repository: FuncionarioRepository) {
    fun cadastrar(dto: FuncionarioDto): FuncionarioDto {
        val funcionario =
            Funcionario
                .criar(
                    nome = dto.nome,
                    cargo = dto.cargo,
                )

        val response = repository.salvar(funcionario)
        return response.toDto()
    }

    fun listarTodos(): List<FuncionarioDto> {
        val response = repository.listarTodos()
        return response.map { it.toDto() }
    }

    fun buscarPorId(id: String): FuncionarioDto? {
        val resultado = repository.buscarPorId(Id.fromString(id))

        return resultado?.toDto()
    }

    fun buscarPorNome(nome: String): FuncionarioDto? {
        val resultado = repository.buscarPorNome(nome)

        return resultado?.toDto()
    }

    fun editar(id: String, dto: FuncionarioDto): FuncionarioDto {
        val funcionario =
            Funcionario.reconstruir(
                id = id,
                nome = dto.nome,
                cargo = dto.cargo,
            )

        val response = repository.editar(funcionario)
        return response.toDto()
    }

    fun deletar(id: String) = repository.deletar(Id.fromString(id))
}
