package br.com.fiap.oficina.servico.domain.repositories

interface SequenciaOrdemServicoRepository {
    fun obterProximoValor(): Long
}
