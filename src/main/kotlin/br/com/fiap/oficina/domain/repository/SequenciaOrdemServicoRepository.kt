package br.com.fiap.oficina.domain.repository

interface SequenciaOrdemServicoRepository {
    fun obterProximoValor(): Long
}
