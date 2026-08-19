package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.application.port.`in`.CriarServicoCommand
import br.com.fiap.oficina.application.port.`in`.CriarServicoUseCase
import br.com.fiap.oficina.application.port.out.ServicoRepository
import br.com.fiap.oficina.domain.entity.Servico
import org.springframework.stereotype.Service

@Service
class CriarServicoService(
    private val servicoRepository: ServicoRepository,
) : CriarServicoUseCase {
    override fun executar(command: CriarServicoCommand): Servico {
        val servico =
            Servico.criar(
                descricao = command.descricao,
                valor = command.valor,
            )

        return servicoRepository.salvar(servico)
    }
}