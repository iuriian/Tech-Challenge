package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.PecaServicoDto
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.servico.domain.entities.OrdemServico
import br.com.fiap.oficina.servico.domain.entities.PecaServico
import br.com.fiap.oficina.servico.domain.enums.OrdemServicoStatus
import br.com.fiap.oficina.servico.domain.enums.TipoItemOrcamento
import br.com.fiap.oficina.servico.domain.valueobjects.ItemOrcamento
import br.com.fiap.oficina.servico.domain.valueobjects.Orcamento
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class ServicoMapperTest {
    private val mapper = ServicoMapper()

    @Test
    fun `deve mapear OrdemServico para ServicoDto`() {
        val funcionarioId = Id.generate()
        val clienteId = Id.generate()
        val veiculoId = Id.generate()

        val peca =
            Peca(
                id = Id.generate(),
                codigo = "PEC001",
                nome = "Filtro",
                precoDeVenda = BigDecimal.TEN,
            )

        val pecaServico = PecaServico.criar(peca, BigDecimal("2"))

        val ordemServico =
            OrdemServico(
                id = Id.generate(),
                descricao = "Troca de óleo",
                status = OrdemServicoStatus.EM_EXECUCAO,
                funcionarioId = funcionarioId,
                clienteId = clienteId,
                veiculoId = veiculoId,
                pecas = listOf(pecaServico),
            )

        val dto = mapper.toResponse(ordemServico)

        assertEquals(ordemServico.id.valor, dto.id)
        assertEquals("Troca de óleo", dto.descricao)
        assertEquals(OrdemServicoStatus.EM_EXECUCAO, dto.status)
        assertEquals(funcionarioId.valor.toString(), dto.funcionarioId)
        assertEquals(clienteId.valor.toString(), dto.clienteId)
        assertEquals(veiculoId.valor.toString(), dto.veiculoId)
        assertEquals(
            listOf(
                PecaServicoDto(
                    peca.id.valor.toString(),
                    BigDecimal("2"),
                ),
            ),
            dto.pecas,
        )
    }

    @Test
    fun `deve mapear Orcamento para OrcamentoDto`() {
        val ordemServicoId = Id.generate()
        val pecaId = Id.generate()
        val orcamento =
            Orcamento(
                ordemServicoId = ordemServicoId,
                itens =
                listOf(
                    ItemOrcamento(
                        tipo = TipoItemOrcamento.PECA,
                        referenciaId = pecaId,
                        descricao = "Filtro",
                        valorUnitario = BigDecimal.TEN,
                        quantidade = BigDecimal("2"),
                        codigoReferencia = "PEC001",
                    ),
                ),
            )

        val dto = mapper.toResponse(orcamento)

        assertEquals(ordemServicoId.valor, dto.servicoId)
        assertEquals(BigDecimal("20"), dto.valorTotal)
        assertEquals(1, dto.itens.size)

        val item = dto.itens.first()

        assertEquals(pecaId.valor, item.pecaId)
        assertEquals("PEC001", item.codigo)
        assertEquals("Filtro", item.nome)
        assertEquals(BigDecimal.TEN, item.precoUnitario)
        assertEquals(BigDecimal("2"), item.quantidade)
        assertEquals(BigDecimal("20"), item.subtotal)
    }
}
