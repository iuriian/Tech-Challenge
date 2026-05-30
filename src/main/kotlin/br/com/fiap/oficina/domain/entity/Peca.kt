package br.com.fiap.oficina.domain.entity

import java.math.BigDecimal

class Peca {

    var id: Long? = null

    lateinit var codigo: String

    lateinit var nome: String

    var descricao: String? = null

    var fabricante: String? = null

    var fornecedor: String? = null

    var precoDeCompra: BigDecimal? = null

    var precoDeVenda: BigDecimal = BigDecimal.ZERO

    var qtdEstoque: Int = 0

    var ativo: Boolean = true

    fun desativar() {
        ativo = false
    }

    fun reativar() {
        ativo = true
    }

    fun retirarPecas(qtd: Int) {
        require(qtd > 0) { "Quantidade para retirada deve ser maior que zero" }
        require(qtd <= qtdEstoque) { "Quantidade em estoque insuficiente" }
        qtdEstoque -= qtd
    }

    fun reporPecas(qtd: Int) {
        require(qtd > 0) { "Quantidade para reposição deve ser maior que zero" }
        qtdEstoque += qtd
    }

}
