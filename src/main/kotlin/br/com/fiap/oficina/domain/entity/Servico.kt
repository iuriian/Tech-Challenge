package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.enum.ServicoStatus
import br.com.fiap.oficina.domain.valueobject.Id

data class Servico(
    val id: Id,
    val descricao: String,
    val status: ServicoStatus = ServicoStatus.RECEBIDA,
    val funcionarioId: Long,
    val cliente: Cliente,
    val veiculo: Veiculo,
    val pecas: List<Peca> = emptyList()
) {

    companion object {
        fun criar(
            descricao: String,
            funcionarioId: Long,
            cliente: Cliente,
            veiculo: Veiculo,
            status: ServicoStatus = ServicoStatus.RECEBIDA,
            pecas: List<Peca> = emptyList()
        ): Servico {
            require(descricao.isNotBlank()) { "Descrição do serviço é obrigatória" }

            return Servico(
                id = Id.gerar(),
                descricao = descricao,
                status = status,
                funcionarioId = funcionarioId,
                cliente = cliente,
                veiculo = veiculo,
                pecas = pecas
            )
        }
    }

    fun adicionarPeca(peca: Peca): Servico = copy(pecas = pecas + peca)

    fun alterarStatus(novoStatus: ServicoStatus): Servico = copy(status = novoStatus)
}
