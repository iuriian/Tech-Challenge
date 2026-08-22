package br.com.fiap.oficina.application.usecase.peca

import br.com.fiap.oficina.application.port.out.PecaRepository
import br.com.fiap.oficina.domain.entity.Peca
import org.springframework.stereotype.Service

@Service
class AtualizarPecaUseCase(
    private val repository: PecaRepository,
) {
    fun executar(
        codigo: String,
        dadosAtualizados: Peca,
    ): Peca {
        val peca =
            repository.buscarAtivoPorCodigo(codigo)
                ?: throw IllegalArgumentException("Peça não encontrada")

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
