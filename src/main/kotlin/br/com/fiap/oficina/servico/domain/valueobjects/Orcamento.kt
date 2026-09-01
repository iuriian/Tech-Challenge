package br.com.fiap.oficina.servico.domain.valueobjects

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.servico.domain.enums.TipoItemOrcamento
import java.math.BigDecimal

data class ItemOrcamento(
    val tipo: TipoItemOrcamento,
    val referenciaId: Id,
    val descricao: String,
    val valorUnitario: BigDecimal,
    val quantidade: BigDecimal,
    val codigoReferencia: String? = null,
) {
    init {
        require(descricao.isNotBlank()) {
            "Descrição do item do orçamento é obrigatória"
        }
        require(valorUnitario >= BigDecimal.ZERO) {
            "Valor unitário do item não pode ser negativo"
        }
        require(quantidade > BigDecimal.ZERO) {
            "Quantidade do item deve ser maior que zero"
        }
        require(tipo != TipoItemOrcamento.PECA || !codigoReferencia.isNullOrBlank()) {
            "Código da peça é obrigatório"
        }
    }

    val subtotal: BigDecimal
        get() = valorUnitario.multiply(quantidade)

    companion object {
        fun dePeca(peca: Peca, quantidade: BigDecimal): ItemOrcamento = ItemOrcamento(
            tipo = TipoItemOrcamento.PECA,
            referenciaId = peca.id,
            descricao = peca.nome,
            valorUnitario = peca.precoDeVenda,
            quantidade = quantidade,
            codigoReferencia = peca.codigo,
        )
    }

    @Deprecated(
        "Use o construtor genérico ou ItemOrcamento.dePeca",
        replaceWith = ReplaceWith(
            "ItemOrcamento(TipoItemOrcamento.PECA, pecaId, nome, precoUnitario, quantidade, codigo)",
            "br.com.fiap.oficina.servico.domain.enums.TipoItemOrcamento",
        ),
    )
    constructor(
        pecaId: Id,
        codigo: String,
        nome: String,
        precoUnitario: BigDecimal,
        quantidade: BigDecimal,
        subtotal: BigDecimal,
    ) : this(
        tipo = TipoItemOrcamento.PECA,
        referenciaId = pecaId,
        descricao = nome,
        valorUnitario = precoUnitario,
        quantidade = quantidade,
        codigoReferencia = codigo,
    ) {
        require(subtotal.compareTo(this.subtotal) == 0) {
            "Subtotal informado é incompatível com valor unitário e quantidade"
        }
    }

    @Deprecated(
        "Use referenciaId",
        replaceWith = ReplaceWith("referenciaId"),
    )
    val pecaId: Id
        get() = referenciaId

    @Deprecated(
        "Use codigoReferencia",
        replaceWith = ReplaceWith("codigoReferencia"),
    )
    val codigo: String
        get() = codigoReferencia.orEmpty()

    @Deprecated(
        "Use descricao",
        replaceWith = ReplaceWith("descricao"),
    )
    val nome: String
        get() = descricao

    @Deprecated(
        "Use valorUnitario",
        replaceWith = ReplaceWith("valorUnitario"),
    )
    val precoUnitario: BigDecimal
        get() = valorUnitario
}

data class Orcamento(val ordemServicoId: Id, val itens: List<ItemOrcamento>) {
    val valorTotal: BigDecimal
        get() =
            itens.fold(BigDecimal.ZERO) { total, item ->
                total + item.subtotal
            }
}
