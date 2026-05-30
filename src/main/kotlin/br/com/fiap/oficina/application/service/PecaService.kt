package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.repository.PecaRepository
import org.springframework.stereotype.Service

@Service
class PecaService(private val repository: PecaRepository) {

    fun salvarPeca(peca: Peca): Peca {
        require(!repository.existePorCodigo(peca.codigo)) { "Peça já cadastrada" }

        return repository.salvar(peca)
    }

    fun atualizarPeca(codigo: String, dadosAtualizados: Peca): Peca {
        val peca = buscarPorCodigo(codigo)

        peca.nome = dadosAtualizados.nome
        peca.descricao = dadosAtualizados.descricao
        peca.fabricante = dadosAtualizados.fabricante
        peca.fornecedor = dadosAtualizados.fornecedor
        peca.precoDeCompra = dadosAtualizados.precoDeCompra
        peca.precoDeVenda = dadosAtualizados.precoDeVenda

        return repository.salvar(peca)
    }

    fun retirarPecas(codigo: String, qtd: Int): Peca? {
        val peca = buscarPorCodigo(codigo)

        peca.retirarPecas(qtd)
        return repository.salvar(peca)
    }

    fun reporPecas(codigo: String, qtd: Int): Peca? {
        val peca = buscarPorCodigo(codigo)

        peca.reporPecas(qtd)
        return repository.salvar(peca)
    }

    fun desativarPeca(codigo: String): Boolean {
        val peca = buscarPorCodigo(codigo)

        peca.desativar()
        repository.salvar(peca)
        return true
    }

    fun deletarPeca(codigo: String) = desativarPeca(codigo)

    fun reativarPeca(codigo: String): Boolean {
        val peca = buscarEntreTodosPorCodigo(codigo) ?: return false

        peca.reativar()
        repository.salvar(peca)
        return true
    }

    fun buscarPorCodigo(codigo: String) =
        repository.buscarAtivoPorCodigo(codigo) ?:throw IllegalArgumentException("Peça não encontrada")

    fun buscarPorNome(nome: String) = repository.buscarAtivoPorNome(nome)

    fun existePorCodigo(codigo: String) = repository.existeAtivoPorCodigo(codigo)

    fun buscarGerencialPorId(id: Long) = repository.buscarPorId(id)

    fun buscarEntreTodosPorCodigo(codigo: String) = repository.buscarPorCodigo(codigo)

    fun existeEntreTodosPorCodigo(codigo: String) = repository.existePorCodigo(codigo)

    fun listarPecas() = repository.listarAtivos()

}
