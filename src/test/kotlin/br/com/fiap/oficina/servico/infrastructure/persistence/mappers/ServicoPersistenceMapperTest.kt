package br.com.fiap.oficina.servico.infrastructure.persistence.mappers

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.mapper.PecaPersistenceMapper
import br.com.fiap.oficina.servico.domain.entities.OrdemServico
import br.com.fiap.oficina.servico.domain.entities.PecaServico
import br.com.fiap.oficina.servico.domain.enums.OrdemServicoStatus
import br.com.fiap.oficina.servico.domain.valueobjects.NumeroOrdemServico
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Duration
import kotlin.test.assertEquals

class ServicoPersistenceMapperTest {
    private val mapper =
        ServicoPersistenceMapper(
            PecaPersistenceMapper(),
        )

    private val funcionarioId = Id.generate()
    private val clienteId = Id.generate()
    private val veiculoId = Id.generate()

    @Test
    fun `deve fazer round-trip de ordem de servico com pecas numero e prazo`() {
        val osNumber =
            NumeroOrdemServico(
                "OS-2026-000123",
            )

        val prazo = Duration.ofMinutes(90)

        val ordemServico =
            OrdemServico(
                id = Id.generate(),
                osNumber = osNumber,
                descricao = "Troca de óleo",
                status = OrdemServicoStatus.EM_EXECUCAO,
                funcionarioId = funcionarioId,
                clienteId = clienteId,
                veiculoId = veiculoId,
                pecas =
                listOf(
                    PecaServico(
                        peca =
                        Peca(
                            id = Id.generate(),
                            codigo = "PEC001",
                            nome = "Filtro",
                            precoDeVenda = BigDecimal.TEN,
                        ),
                        quantidade = BigDecimal("2"),
                    ),
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
        assertEquals(BigDecimal("2"), jpa.pecas.first().quantidade)

        val resultado = mapper.toDomain(jpa)

        assertEquals(osNumber, resultado.osNumber)
        assertEquals(prazo, resultado.prazo)
        assertEquals(ordemServico, resultado)
    }

    @Test
    fun `deve usar status padrao quando jpa possui status nulo`() {
        val ordemServico =
            OrdemServico(
                id = Id.generate(),
                descricao = "Revisão",
                status = OrdemServicoStatus.RECEBIDA,
                funcionarioId = funcionarioId,
                clienteId = clienteId,
                veiculoId = veiculoId,
            )

        val jpa =
            mapper.toJpa(ordemServico).apply {
                status = null
            }

        val resultado = mapper.toDomain(jpa)

        assertEquals(OrdemServicoStatus.RECEBIDA, resultado.status)
        assertEquals(funcionarioId, resultado.funcionarioId)
        assertEquals(clienteId, resultado.clienteId)
        assertEquals(veiculoId, resultado.veiculoId)
    }
}
