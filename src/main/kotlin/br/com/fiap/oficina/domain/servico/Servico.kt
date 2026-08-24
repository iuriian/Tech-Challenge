package br.com.fiap.oficina.domain.servico

import br.com.fiap.oficina.domain.valueobject.Id
import java.math.BigDecimal

data class Servico(
    val id: Id,
    val descricao: String,
    val valor: BigDecimal,
    val ativo: Boolean = true,
) {
    init {
        require(descricao.isNotBlank()) {
            "Descrição do serviço é obrigatória"
        }

        require(valor >= BigDecimal.ZERO) {
            "Valor do serviço não pode ser negativo"
        }
    }

    companion object {
        fun criar(
            descricao: String,
            valor: BigDecimal,
        ): Servico =
            Servico(
                id = Id.generate(),
                descricao = descricao,
                valor = valor,
            )
    }

    fun alterarDescricao(novaDescricao: String): Servico =
        copy(descricao = novaDescricao)

    fun alterarValor(novoValor: BigDecimal): Servico =
        copy(valor = novoValor)

    fun desativar(): Servico =
        copy(ativo = false)

    fun reativar(): Servico =
        copy(ativo = true)
}