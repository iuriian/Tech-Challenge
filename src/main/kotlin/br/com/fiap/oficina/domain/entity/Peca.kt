package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.valueobject.Id
import java.math.BigDecimal

data class Peca(
    val id: Id,
    val codigo: String,
    val nome: String,
    val descricao: String? = null,
    val fabricante: String? = null,
    val fornecedor: String? = null,
    val precoDeCompra: BigDecimal? = null,
    val precoDeVenda: BigDecimal,
    val qtdEstoque: Int = 0,
    val ativo: Boolean = true,
) {
    companion object {
        fun criar(
            codigo: String,
            nome: String,
            descricao: String? = null,
            fabricante: String? = null,
            fornecedor: String? = null,
            precoDeCompra: BigDecimal? = null,
            precoDeVenda: BigDecimal,
            qtdEstoque: Int,
        ): Peca {
            require(codigo.isNotBlank()) { "Código da peça é obrigatório" }
            require(nome.isNotBlank()) { "Nome da peça é obrigatório" }
            require(precoDeCompra == null || precoDeCompra >= BigDecimal.ZERO) {
                "Preço de compra não pode ser negativo"
            }
            require(precoDeVenda >= BigDecimal.ZERO) { "Preço de venda não pode ser negativo" }
            require(qtdEstoque >= 0) { "Quantidade em estoque não pode ser negativa" }

            return Peca(
                id = Id.generate(),
                codigo = codigo,
                nome = nome,
                descricao = descricao,
                fabricante = fabricante,
                fornecedor = fornecedor,
                precoDeCompra = precoDeCompra,
                precoDeVenda = precoDeVenda,
                qtdEstoque = qtdEstoque,
            )
        }
    }

    fun desativar(): Peca = copy(ativo = false)

    fun reativar(): Peca = copy(ativo = true)

    fun retirarPecas(qtd: Int): Peca {
        require(qtd > 0) { "Quantidade para retirada deve ser maior que zero" }
        require(qtd <= qtdEstoque) { "Quantidade em estoque insuficiente" }
        return copy(qtdEstoque = qtdEstoque - qtd)
    }

    fun reporPecas(qtd: Int): Peca {
        require(qtd > 0) { "Quantidade para reposição deve ser maior que zero" }
        return copy(qtdEstoque = qtdEstoque + qtd)
    }
}
