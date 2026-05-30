package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.enum.ServicoStatus

class Servico {

    var id: Long? = null

    lateinit var descricao: String

    var status: ServicoStatus? = null

    var funcionarioId: String? = null

    lateinit var cliente: Cliente

    var veiculoId: Long? = null

    var pecasIds: List<Long> = mutableListOf()
}
