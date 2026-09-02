package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.OrdemServico
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.domain.valueobject.ItemOrcamento
import br.com.fiap.oficina.domain.valueobject.NumeroOrdemServico
import br.com.fiap.oficina.domain.valueobject.Orcamento
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Duration
import kotlin.test.assertEquals

class OrdemServicoPersistenceMapperTest {
    private val mapper = OrdemServicoPersistenceMapper()

    private val funcionarioId = Id.generate()
    private val clienteId = Id.generate()
    private val veiculoId = Id.generate()

    @Test
    fun `deve fazer round-trip de ordem de servico com orcamento numero e prazo`() {
        val osNumber =
            NumeroOrdemServico(
                "OS-2026-000123",
            )

        val prazo = Duration.ofMinutes(90)

        val peca =
            Peca(
                id = Id.generate(),
                codigo = "PEC001",
                nome = "Filtro",
                precoDeVenda = BigDecimal.TEN,
            )

        val item =
            ItemOrcamento.dePeca(
                peca = peca,
                quantidade = BigDecimal("2"),
            )

        val ordemServico =
            OrdemServico(
                id = Id.generate(),
                osNumber = osNumber,
                descricao = "Troca de óleo",
                status = OrdemServicoStatus.EM_EXECUCAO,
                funcionarioId = funcionarioId,
                clienteId = clienteId,
                veiculoId = veiculoId,
                orcamento =
                Orcamento(
                    itens = listOf(item),
                ),
                prazo = prazo,
            )

        val jpa = mapper.toJpa(ordemServico)

        assertEquals(ordemServico.id.valor, jpa.id)
        assertEquals("OS-2026-000123", jpa.osNumber)
        assertEquals(90L, jpa.prazoMinutos)
        assertEquals(OrdemServicoStatus.EM_EXECUCAO, jpa.status)
        assertEquals(funcionarioId.valor, jpa.funcionarioId)
        assertEquals(clienteId.valor, jpa.clienteId)
        assertEquals(veiculoId.valor, jpa.veiculoId)
        assertEquals(1, jpa.itens.size)
        assertEquals(BigDecimal("2"), jpa.itens.first().quantidade)
        assertEquals("PEC001", jpa.itens.first().codigoReferencia)

        val resultado = mapper.toDomain(jpa)

        assertEquals(osNumber, resultado.osNumber)
        assertEquals(prazo, resultado.prazo)
        assertEquals(ordemServico, resultado)
    }
}
