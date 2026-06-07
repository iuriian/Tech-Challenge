package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.enum.ServicoStatus

class Servico {

    var id: Long? = null

    lateinit var descricao: String

    var status: ServicoStatus? = null

    var funcionarioId: Long? = null

    lateinit var cliente: Cliente

    lateinit var veiculo: Veiculo

    var pecas: List<Peca> = mutableListOf()
}
