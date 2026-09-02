package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.jpa.FuncionarioJpaRepository
import br.com.fiap.oficina.infrastructure.persistence.mapper.toDomain
import br.com.fiap.oficina.infrastructure.persistence.mapper.toEntity
import org.springframework.stereotype.Component

@Component
class FuncionarioRepositoryImpl(private val repository: FuncionarioJpaRepository) : FuncionarioRepository {
    override fun salvar(funcionario: Funcionario): Funcionario {
        val resultado = repository.save(funcionario.toEntity())
        return resultado.toDomain()
    }

    override fun listarTodos(): List<Funcionario> = repository.findAll().map { it.toDomain() }

    override fun buscarPorId(id: Id): Funcionario? = repository.findById(id.valor).map { it.toDomain() }.orElse(null)

    override fun buscarPorNome(nome: String): Funcionario? = repository.findByNome(nome)?.toDomain()

    override fun editar(funcionario: Funcionario): Funcionario {
        val entity = repository.findById(funcionario.id.valor)
            .orElseThrow { IllegalStateException("Funcionário ${funcionario.id.valor} não encontrado para edição") }
        entity.nome = funcionario.nome
        entity.cargo = funcionario.cargo.id
        return repository.save(entity).toDomain()
    }

    override fun deletar(id: Id) {
        repository.deleteById(id.valor)
    }
}
