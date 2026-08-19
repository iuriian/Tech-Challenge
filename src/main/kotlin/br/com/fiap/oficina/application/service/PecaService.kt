package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.repository.PecaRepository
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service

@Service
class PecaService(
    private val repository: PecaRepository,
) {
    fun salvarPeca(peca: Peca): Peca {
        require(!repository.existePorCodigo(peca.codigo)) { "Peça já cadastrada" }

        return repository.salvar(peca)
    }

    fun atualizarPeca(
        codigo: String,
        dadosAtualizados: Peca,
    ): Peca {
        val peca = buscarPorCodigo(codigo)

        return repository.salvar(
            peca.copy(
                nome = dadosAtualizados.nome,
                descricao = dadosAtualizados.descricao,
                fabricante = dadosAtualizados.fabricante,
                fornecedor = dadosAtualizados.fornecedor,
                precoDeCompra = dadosAtualizados.precoDeCompra,
                precoDeVenda = dadosAtualizados.precoDeVenda,
            ),
        )
    }

    fun retirarPecas(
        codigo: String,
        qtd: Int,
    ): Peca? {
        val peca = buscarPorCodigo(codigo)

        return repository.salvar(peca.retirarPecas(qtd))
    }

    fun reporPecas(
        codigo: String,
        qtd: Int,
    ): Peca? {
        val peca = buscarPorCodigo(codigo)

        return repository.salvar(peca.reporPecas(qtd))
    }

    fun desativarPeca(codigo: String): Boolean {
        val peca = buscarPorCodigo(codigo)

        repository.salvar(peca.desativar())
        return true
    }

    fun deletarPeca(codigo: String) = desativarPeca(codigo)

    fun reativarPeca(codigo: String): Boolean {
        val peca = buscarEntreTodosPorCodigo(codigo) ?: return false

        repository.salvar(peca.reativar())
        return true
    }

    fun buscarPorCodigo(codigo: String) =
        repository.buscarAtivoPorCodigo(codigo) ?: throw IllegalArgumentException("Peça não encontrada")

    fun buscarPorNome(nome: String) = repository.buscarAtivoPorNome(nome)

    fun existePorCodigo(codigo: String) = repository.existeAtivoPorCodigo(codigo)

    fun buscarGerencialPorId(id: Id) = repository.buscarPorId(id)

    fun buscarEntreTodosPorCodigo(codigo: String) = repository.buscarPorCodigo(codigo)

    fun existeEntreTodosPorCodigo(codigo: String) = repository.existePorCodigo(codigo)

    fun listarPecas() = repository.listarAtivos()
}
