package br.com.fiap.oficina.application

import br.com.fiap.oficina.infrastructure.persistence.entity.Peca
import br.com.fiap.oficina.infrastructure.persistence.repository.PecaRepository
import org.springframework.stereotype.Service

@Service
class PecaService(private val repository: PecaRepository) {

    fun salvarPeca(peca: Peca): Peca {
        if (repository.existsByCodigo(peca.codigo)) {
            throw IllegalArgumentException("Peça já cadastrada")
        }

        return repository.save(peca)
    }

    fun atualizarPeca(codigo: String, dadosAtualizados: Peca): Peca? {
        val peca = buscarPorCodigo(codigo) ?: return null

        peca.nome = dadosAtualizados.nome
        peca.descricao = dadosAtualizados.descricao
        peca.fabricante = dadosAtualizados.fabricante
        peca.fornecedor = dadosAtualizados.fornecedor
        peca.precoDeCompra = dadosAtualizados.precoDeCompra
        peca.precoDeVenda = dadosAtualizados.precoDeVenda

        return repository.save(peca)
    }

    fun retirarPecas(codigo: String, qtd: Int): Peca? {
        val peca = buscarPorCodigo(codigo) ?: return null

        peca.retirarPecas(qtd)
        return repository.save(peca)
    }

    fun reporPecas(codigo: String, qtd: Int): Peca? {
        val peca = buscarPorCodigo(codigo) ?: return null

        peca.reporPecas(qtd)
        return repository.save(peca)
    }

    fun desativarPeca(codigo: String): Boolean {
        val peca = buscarPorCodigo(codigo) ?: return false

        peca.desativar()
        repository.save(peca)
        return true
    }

    fun deletarPeca(codigo: String) = desativarPeca(codigo)

    fun reativarPeca(codigo: String): Boolean {
        val peca = buscarEntreTodosPorCodigo(codigo) ?: return false

        peca.reativar()
        repository.save(peca)
        return true
    }

    fun buscarPorCodigo(codigo: String) = repository.findByCodigoAndAtivoTrue(codigo)

    fun buscarPorNome(nome: String) = repository.findByNomeIgnoreCaseAndAtivoTrue(nome)

    fun existePorCodigo(codigo: String) = repository.existsByCodigoAndAtivoTrue(codigo)

    fun buscarGerencialPorId(id: Long) = repository.findById(id).orElse(null)

    fun buscarEntreTodosPorCodigo(codigo: String) = repository.findByCodigo(codigo)

    fun existeEntreTodosPorCodigo(codigo: String) = repository.existsByCodigo(codigo)

    fun listarPecas() = repository.findAllByAtivoTrue()

}
