package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.exception.PecaNaoEncontradoException
import br.com.fiap.oficina.domain.repository.PecaRepository

class AtualizarPecaUseCase(private val repository: PecaRepository) {
    fun executar(codigo: String, dadosAtualizados: Peca): Peca {
        val peca =
            repository.buscarAtivoPorCodigo(codigo)
                ?: throw PecaNaoEncontradoException.porCodigo(codigo)

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
}
