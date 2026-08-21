package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.mapper.toDomain
import br.com.fiap.oficina.infrastructure.persistence.mapper.toEntity
import br.com.fiap.oficina.infrastructure.persistence.jpa.FuncionarioRepositoryJpa
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Component

@Component
class FuncionarioRepositoryAdapter(private val repository: FuncionarioRepositoryJpa) : FuncionarioRepository {
    override fun salvar(funcionario: Funcionario): Funcionario = try {
        val resultado = repository.save(funcionario.toEntity())
        resultado.toDomain()
    } catch (ex: Exception) {
        throw Exception("Não foi possível salvar o funcionário!")
    }

    override fun listarTodos(): List<Funcionario> = try {
        repository.findAll().map { it.toDomain() }
    } catch (ex: Exception) {
        throw EntityNotFoundException("Não há funcionários cadastrados!")
    }

    override fun buscarPorId(id: Id): Funcionario? = try {
        repository.findById(id.valor).map { it.toDomain() }.orElse(null)
    } catch (e: EntityNotFoundException) {
        throw EntityNotFoundException("Funcionário não encontrado!")
    }

    override fun buscarPorNome(nome: String): Funcionario? = try {
        repository.findByNome(nome)?.toDomain()
    } catch (e: EntityNotFoundException) {
        throw EntityNotFoundException("Funcionário não encontrado!")
    }

    override fun editar(funcionario: Funcionario): Funcionario {
        val resultado =
            repository.findById(funcionario.id.valor).orElse(null)
                ?: throw EntityNotFoundException("Funcionário não encontrado!")

        resultado.nome = funcionario.nome
        resultado.cargo = funcionario.cargo.id

        return repository.save(resultado).toDomain()
    }

    override fun deletar(id: Id) = try {
        repository.deleteById(id.valor)
    } catch (e: Exception) {
        throw Exception("Não foi possível deletar o funcionário!")
    }
}
