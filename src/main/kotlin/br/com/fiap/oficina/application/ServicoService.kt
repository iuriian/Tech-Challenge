package br.com.fiap.oficina.application

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.ServicoRepository

class ServicoService (
    private val repository: ServicoRepository,
    private val clienteRepository: ClienteRepository
) {

    fun salvar(servico: Servico) : Servico {
        return repository.save(servico)
    }

    fun listarPorId(id: Long) : Servico? {
        return repository.findById(id).orElse(null)
    }

    fun listarTodos() : List<Servico> {
        return repository.findAll()
    }

    fun deletarPorId(id: Long) : String? {
        if(!repository.existsById(id)){
            throw IllegalArgumentException("Serviço não encontrado para deletar.")
        }
        repository.deleteById(id)

        return "Servico deletado."
    }

}