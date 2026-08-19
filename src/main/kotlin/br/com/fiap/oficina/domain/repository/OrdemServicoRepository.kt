package br.com.fiap.oficina.domain.repository

import br.com.fiap.oficina.domain.entity.OrdemServico
import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.domain.valueobject.Id
import java.time.Instant

interface OrdemServicoRepository {

    fun salvar(ordemServico: OrdemServico): OrdemServico

    fun buscarPorId(id: Id): OrdemServico?

    fun listarTodos(): List<OrdemServico>

    fun listarPorStatus(status: OrdemServicoStatus): List<OrdemServico>

    fun listarPorCliente(clienteId: Id): List<OrdemServico>

    fun listarPorVeiculo(veiculoId: Id): List<OrdemServico>

    fun listarPorDataAberturaEntre(
        inicio: Instant,
        fim: Instant,
    ): List<OrdemServico>

    fun existePorId(id: Id): Boolean

    fun deletarPorId(id: Id)
}
