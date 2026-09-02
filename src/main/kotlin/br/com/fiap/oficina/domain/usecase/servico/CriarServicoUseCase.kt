package br.com.fiap.oficina.domain.usecase.servico

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.repository.ServicoRepository

class CriarServicoUseCase(private val repository: ServicoRepository) {
    fun executar(servico: Servico): Servico = repository.salvar(servico)
}
