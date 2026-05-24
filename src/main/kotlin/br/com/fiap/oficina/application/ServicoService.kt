package br.com.fiap.oficina.application

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.ServicoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ServicoService (
    private val repository: ServicoRepository,
    private val clienteRepository: ClienteRepository
) {

    @Transactional
    fun salvar(servico: Servico, clienteId: Long) : Servico {
        val cliente = clienteRepository.findById(clienteId)
            .orElseThrow { IllegalArgumentException("Cliente não encontrado com o ID: $clienteId") }
        servico.cliente = cliente
        return repository.save(servico)
    }

    fun listarPorId(id: Long) : Servico? {
        return repository.findById(id).orElse(null)
    }

    fun listarTodos() : List<Servico> {
        return repository.findAll()
    }

    @Transactional
    fun deletarPorId(id: Long) : String? {
        if(!repository.existsById(id)){
            throw IllegalArgumentException("Serviço não encontrado para deletar.")
        }
        repository.deleteById(id)

        return "Servico deletado."
    }

}