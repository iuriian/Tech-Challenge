package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.ItemOrcamentoRequest
import br.com.fiap.oficina.application.dto.OrdemServicoRequest
import br.com.fiap.oficina.domain.entity.OrdemServico
import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.domain.enum.TipoItemOrcamento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.domain.valueobject.ItemOrcamento
import br.com.fiap.oficina.domain.valueobject.Orcamento
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class OrdemServicoMapperTest {
    private val mapper =
        OrdemServicoMapper(
            ItemOrcamentoMapper(),
        )

    @Test
    fun `deve mapear ordem de servico para response`() {
        val funcionarioId = Id.generate()
        val clienteId = Id.generate()
        val veiculoId = Id.generate()
        val referenciaId = Id.generate()

        val ordemServico =
            OrdemServico(
                id = Id.generate(),
                descricao = "Troca de óleo",
                status = OrdemServicoStatus.EM_EXECUCAO,
                funcionarioId = funcionarioId,
                clienteId = clienteId,
                veiculoId = veiculoId,
                orcamento =
                Orcamento(
                    itens =
                    listOf(
                        ItemOrcamento(
                            tipo = TipoItemOrcamento.PECA,
                            referenciaId = referenciaId,
                            descricao = "Filtro",
                            valorUnitario = BigDecimal.TEN,
                            quantidade = BigDecimal("2"),
                            codigoReferencia = "PEC001",
                        ),
                    ),
                ),
            )

        val response = mapper.toResponse(ordemServico)

        assertEquals(ordemServico.id.valor, response.id)
        assertEquals("Troca de óleo", response.descricao)
        assertEquals(OrdemServicoStatus.EM_EXECUCAO, response.status)
        assertEquals(funcionarioId.valor.toString(), response.funcionarioId)
        assertEquals(clienteId.valor.toString(), response.clienteId)
        assertEquals(veiculoId.valor.toString(), response.veiculoId)
        assertEquals(1, response.itens.size)

        val item = response.itens.single()

        assertEquals(TipoItemOrcamento.PECA, item.tipo)
        assertEquals(referenciaId.valor, item.referenciaId)
        assertEquals(BigDecimal("2"), item.quantidade)
    }

    @Test
    fun `deve mapear request para comando`() {
        val id = Id.generate()
        val funcionarioId = Id.generate()
        val clienteId = Id.generate()
        val veiculoId = Id.generate()
        val referenciaId = Id.generate()

        val request =
            OrdemServicoRequest(
                descricao = "Revisão",
                funcionarioId = funcionarioId.valor.toString(),
                clienteId = clienteId.valor.toString(),
                veiculoId = veiculoId.valor.toString(),
                itens =
                listOf(
                    ItemOrcamentoRequest(
                        tipo = TipoItemOrcamento.PECA,
                        referenciaId = referenciaId.valor.toString(),
                        quantidade = BigDecimal("3"),
                    ),
                ),
            )

        val comando =
            mapper.toCommand(
                request = request,
                id = id,
            )

        assertEquals(id, comando.id)
        assertEquals("Revisão", comando.descricao)
        assertEquals(OrdemServicoStatus.RECEBIDA, comando.status)
        assertEquals(funcionarioId, comando.funcionarioId)
        assertEquals(clienteId, comando.clienteId)
        assertEquals(veiculoId, comando.veiculoId)
        assertEquals(1, comando.itens.size)

        val item = comando.itens.single()

        assertEquals(TipoItemOrcamento.PECA, item.tipo)
        assertEquals(referenciaId, item.referenciaId)
        assertEquals(BigDecimal("3"), item.quantidade)
    }

    @Test
    fun `deve assumir RECEBIDA quando status nao for informado`() {
        val request =
            OrdemServicoRequest(
                descricao = "Revisão",
                funcionarioId = Id.generate().valor.toString(),
                clienteId = Id.generate().valor.toString(),
                veiculoId = Id.generate().valor.toString(),
            )

        val comando = mapper.toCommand(request)

        assertEquals(
            OrdemServicoStatus.RECEBIDA,
            comando.status,
        )
    }
}
