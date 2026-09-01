package br.com.fiap.oficina.servico.domain.repositories

import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.servico.domain.entities.OrdemServico
import br.com.fiap.oficina.servico.domain.enums.OrdemServicoStatus
import java.time.Instant

interface OrdemServicoRepository {
    fun salvar(ordemServico: OrdemServico): OrdemServico

    fun buscarPorId(id: Id): OrdemServico?

    fun listarTodos(): List<OrdemServico>

    fun listarPorStatus(status: OrdemServicoStatus): List<OrdemServico>

    fun listarPorCliente(clienteId: Id): List<OrdemServico>

    fun listarPorVeiculo(veiculoId: Id): List<OrdemServico>

    fun listarPorDataAberturaEntre(inicio: Instant, fim: Instant): List<OrdemServico>

    fun existePorId(id: Id): Boolean

    fun deletarPorId(id: Id)
}
