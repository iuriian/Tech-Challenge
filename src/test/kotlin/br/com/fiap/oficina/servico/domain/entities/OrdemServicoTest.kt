package br.com.fiap.oficina.servico.domain.entities

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.servico.domain.enums.OrdemServicoStatus
import br.com.fiap.oficina.servico.domain.valueobjects.NumeroOrdemServico
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant

class OrdemServicoTest {
    private val numeroOrdemServico =
        NumeroOrdemServico.criar(
            sequencial = 1,
            ano = 2026,
        )

    private val funcionarioId = Id.generate()
    private val clienteId = Id.generate()
    private val veiculoId = Id.generate()

    private val peca =
        Peca(
            id = Id.generate(),
            codigo = "PEC001",
            nome = "Filtro",
            precoDeVenda = BigDecimal.TEN,
        )

    private fun criarOrdemServico(
        descricao: String = "Revisão",
        status: OrdemServicoStatus = OrdemServicoStatus.RECEBIDA,
        pecas: List<PecaServico> = emptyList(),
        prazo: Duration? = null,
    ): OrdemServico = OrdemServico.criar(
        osNumber = numeroOrdemServico,
        descricao = descricao,
        funcionarioId = funcionarioId,
        clienteId = clienteId,
        veiculoId = veiculoId,
        status = status,
        pecas = pecas,
        prazo = prazo,
    )

    @Test
    fun `deve criar ordem de servico valida com status padrao`() {
        val ordemServico =
            criarOrdemServico(
                descricao = "Troca de óleo",
            )

        assertNotNull(ordemServico.id)
        assertEquals(numeroOrdemServico, ordemServico.osNumber)
        assertEquals("Troca de óleo", ordemServico.descricao)
        assertEquals(OrdemServicoStatus.RECEBIDA, ordemServico.status)
        assertEquals(funcionarioId, ordemServico.funcionarioId)
        assertEquals(clienteId, ordemServico.clienteId)
        assertEquals(veiculoId, ordemServico.veiculoId)
        assertTrue(ordemServico.pecas.isEmpty())
    }

    @Test
    fun `deve criar ordem de servico com status e pecas informados`() {
        val pecaServico = PecaServico.criar(peca, BigDecimal("2"))

        val ordemServico =
            criarOrdemServico(
                descricao = "Troca de óleo",
                status = OrdemServicoStatus.EM_EXECUCAO,
                pecas = listOf(pecaServico),
            )

        assertEquals(OrdemServicoStatus.EM_EXECUCAO, ordemServico.status)
        assertEquals(listOf(pecaServico), ordemServico.pecas)
    }

    @Test
    fun `deve criar ordem de servico com prazo informado`() {
        val prazo = Duration.ofMinutes(90)

        val ordemServico =
            criarOrdemServico(
                prazo = prazo,
            )

        assertEquals(prazo, ordemServico.prazo)
    }

    @Test
    fun `deve rejeitar prazo menor que um minuto`() {
        assertThrows(IllegalArgumentException::class.java) {
            criarOrdemServico(
                prazo = Duration.ofSeconds(30),
            )
        }
    }

    @Test
    fun `deve rejeitar descricao em branco`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                criarOrdemServico(
                    descricao = "",
                )
            }

        assertEquals(
            "Descrição do serviço é obrigatória",
            exception.message,
        )
    }

    @ParameterizedTest
    @CsvSource(
        "RECEBIDA, EM_DIAGNOSTICO",
        "EM_DIAGNOSTICO, AGUARDANDO_APROVACAO",
        "AGUARDANDO_APROVACAO, EM_EXECUCAO",
        "EM_EXECUCAO, FINALIZADA",
        "FINALIZADA, ENTREGUE",
    )
    fun `deve avancar pelo fluxo principal`(atual: OrdemServicoStatus, esperado: OrdemServicoStatus) {
        val ordemServico =
            criarOrdemServico(
                status = atual,
            )

        val avancada = ordemServico.avancarStatus()

        assertEquals(esperado, avancada.status)
        assertEquals(atual, ordemServico.status)
    }

    @Test
    fun `deve permitir cancelamento enquanto aguarda aprovacao`() {
        val ordemServico =
            criarOrdemServico(
                status = OrdemServicoStatus.AGUARDANDO_APROVACAO,
            )

        val cancelada =
            ordemServico.alterarStatus(
                OrdemServicoStatus.CANCELADA,
            )

        assertEquals(
            OrdemServicoStatus.CANCELADA,
            cancelada.status,
        )
        assertEquals(
            OrdemServicoStatus.AGUARDANDO_APROVACAO,
            ordemServico.status,
        )
    }

    @ParameterizedTest
    @CsvSource(
        "RECEBIDA, EM_EXECUCAO",
        "RECEBIDA, CANCELADA",
        "AGUARDANDO_APROVACAO, FINALIZADA",
        "EM_EXECUCAO, CANCELADA",
        "ENTREGUE, CANCELADA",
    )
    fun `deve rejeitar transicoes invalidas`(atual: OrdemServicoStatus, alvo: OrdemServicoStatus) {
        val ordemServico =
            criarOrdemServico(
                status = atual,
            )

        assertThrows(IllegalStateException::class.java) {
            ordemServico.alterarStatus(alvo)
        }

        assertEquals(atual, ordemServico.status)
    }

    @ParameterizedTest
    @EnumSource(
        value = OrdemServicoStatus::class,
        names = ["ENTREGUE", "CANCELADA"],
    )
    fun `nao deve avancar a partir do estado final`(statusFinal: OrdemServicoStatus) {
        val ordemServico =
            criarOrdemServico(
                status = statusFinal,
            )

        assertThrows(IllegalStateException::class.java) {
            ordemServico.avancarStatus()
        }

        assertEquals(statusFinal, ordemServico.status)
    }

    @Test
    fun `deve alterar status preservando imutabilidade`() {
        val ordemServico = criarOrdemServico()

        val emDiagnostico =
            ordemServico.alterarStatus(
                OrdemServicoStatus.EM_DIAGNOSTICO,
            )

        assertEquals(
            OrdemServicoStatus.RECEBIDA,
            ordemServico.status,
        )
        assertEquals(
            OrdemServicoStatus.EM_DIAGNOSTICO,
            emDiagnostico.status,
        )
    }

    @Test
    fun `deve registrar dataInicioExecucao ao transitar para EM_EXECUCAO`() {
        val agora = Instant.now()

        val ordemServico =
            criarOrdemServico(
                status = OrdemServicoStatus.AGUARDANDO_APROVACAO,
            )

        val emExecucao =
            ordemServico.alterarStatus(
                OrdemServicoStatus.EM_EXECUCAO,
                agora,
            )

        assertEquals(agora, emExecucao.dataInicioExecucao)
        assertNull(emExecucao.dataFinalizacao)
    }

    @Test
    fun `deve registrar dataFinalizacao ao transitar para FINALIZADA`() {
        val agora = Instant.now()

        val ordemServico =
            criarOrdemServico(
                status = OrdemServicoStatus.EM_EXECUCAO,
            )

        val finalizada =
            ordemServico.alterarStatus(
                OrdemServicoStatus.FINALIZADA,
                agora,
            )

        assertEquals(agora, finalizada.dataFinalizacao)
        assertNull(finalizada.dataInicioExecucao)
    }

    @Test
    fun `deve preservar timestamps ao transitar para outros status`() {
        val inicio = Instant.now()
        val fim = inicio.plusSeconds(3600)

        val emExecucao =
            criarOrdemServico(
                status = OrdemServicoStatus.AGUARDANDO_APROVACAO,
            ).alterarStatus(
                OrdemServicoStatus.EM_EXECUCAO,
                inicio,
            )

        val finalizada =
            emExecucao.alterarStatus(
                OrdemServicoStatus.FINALIZADA,
                fim,
            )

        val entregue =
            finalizada.alterarStatus(
                OrdemServicoStatus.ENTREGUE,
            )

        assertEquals(inicio, entregue.dataInicioExecucao)
        assertEquals(fim, entregue.dataFinalizacao)
    }

    @Test
    fun `criar deve registrar dataAbertura`() {
        val antes = Instant.now()
        val ordemServico = criarOrdemServico()
        val depois = Instant.now()

        assertFalse(ordemServico.dataAbertura.isBefore(antes))
        assertFalse(ordemServico.dataAbertura.isAfter(depois))
        assertNull(ordemServico.dataInicioExecucao)
        assertNull(ordemServico.dataFinalizacao)
    }

    @Test
    fun `deve gerar orcamento totalizando valor das pecas por quantidade`() {
        val outraPeca =
            Peca(
                id = Id.generate(),
                codigo = "PEC002",
                nome = "Óleo",
                precoDeVenda = BigDecimal("30.00"),
            )

        val ordemServico =
            criarOrdemServico(
                pecas =
                listOf(
                    PecaServico.criar(
                        peca,
                        BigDecimal("2"),
                    ),
                    PecaServico.criar(
                        outraPeca,
                        BigDecimal("1.5"),
                    ),
                ),
            )

        val orcamento = ordemServico.orcamento

        assertEquals(
            ordemServico.id,
            orcamento.ordemServicoId,
        )
        assertEquals(2, orcamento.itens.size)
        assertEquals(
            0,
            BigDecimal("65.0").compareTo(orcamento.valorTotal),
        )

        val itemFiltro =
            orcamento.itens.first {
                it.codigoReferencia == "PEC001"
            }

        assertEquals("Filtro", itemFiltro.descricao)
        assertEquals(BigDecimal.TEN, itemFiltro.valorUnitario)
        assertEquals(BigDecimal("2"), itemFiltro.quantidade)
        assertEquals(
            0,
            BigDecimal("20").compareTo(itemFiltro.subtotal),
        )
    }

    @Test
    fun `deve gerar orcamento zerado para ordem de servico sem pecas`() {
        val ordemServico =
            criarOrdemServico(
                descricao = "Diagnóstico",
            )

        val orcamento = ordemServico.orcamento

        assertTrue(orcamento.itens.isEmpty())
        assertEquals(
            0,
            BigDecimal.ZERO.compareTo(orcamento.valorTotal),
        )
    }
}
