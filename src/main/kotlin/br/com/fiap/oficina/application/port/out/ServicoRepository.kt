package br.com.fiap.oficina.application.port.out

import br.com.fiap.oficina.domain.entity.Servico

interface ServicoRepository {

    fun salvar(servico: Servico): Servico

}