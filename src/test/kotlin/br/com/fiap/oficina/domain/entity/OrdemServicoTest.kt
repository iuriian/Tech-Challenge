package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.enum.Cargo
import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal
import java.time.Instant

class OrdemServicoTest {
    private val cliente =
        Cliente(
            id = Id.generate(),
            nome = "Cliente",
            documento = Documento.cpf("39053344705"),
            email = "cliente@example.com",
        )

    private val veiculo =
        Veiculo(
            id = Id.generate(),
            marca = "Volkswagen",
            nome = "Gol",
            modelo = "Gol 1.6",
            ano = "2020",
            placa = "ABC1D23",
            motorista = cliente,
        )

    private val funcionario =
        Funcionario(
            id = Id.generate(),
            nome = "Funcionario Teste",
            cargo = Cargo.MECANICO,
        )

    private val peca =
        Peca(
            id = Id.generate(),
            codigo = "PEC001",
            nome = "Filtro",
            precoDeVenda = BigDecimal.TEN,
        )

    @Test
    fun `deve criar servico valido com status padrao`() {
        val ordemServico =
            OrdemServico.criar(
                descricao = "Troca de óleo",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
            )

        assertNotNull(ordemServico.id)
        assertEquals("Troca de óleo", ordemServico.descricao)
        assertEquals(OrdemServicoStatus.RECEBIDA, ordemServico.status)
        assertEquals(funcionario, ordemServico.funcionario)
        assertEquals(cliente, ordemServico.cliente)
        assertEquals(veiculo, ordemServico.veiculo)
        assertTrue(ordemServico.pecas.isEmpty())
    }

    @Test
    fun `deve criar servico com status e pecas informados`() {
        val pecaServico = PecaServico.criar(peca, BigDecimal("2"))
        val ordemServico =
            OrdemServico.criar(
                descricao = "Troca de óleo",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
                status = OrdemServicoStatus.EM_EXECUCAO,
                pecas = listOf(pecaServico),
            )

        assertEquals(OrdemServicoStatus.EM_EXECUCAO, ordemServico.status)
        assertEquals(listOf(pecaServico), ordemServico.pecas)
    }

    @Test
    fun `deve rejeitar descricao em branco`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                OrdemServico.criar(
                    descricao = "",
                    funcionario = funcionario,
                    cliente = cliente,
                    veiculo = veiculo,
                )
            }

        assertEquals("Descrição do serviço é obrigatória", exception.message)
    }

    @Test
    fun `deve adicionar peca preservando imutabilidade`() {
        val ordemServico =
            OrdemServico.criar(
                descricao = "Revisão",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
            )

        val comPeca = ordemServico.adicionarPeca(peca, BigDecimal("3"))

        assertTrue(ordemServico.pecas.isEmpty())
        assertEquals(listOf(PecaServico.criar(peca, BigDecimal("3"))), comPeca.pecas)
    }

    @Test
    fun `deve adicionar peca-servico diretamente preservando imutabilidade`() {
        val ordemServico =
            OrdemServico.criar(
                descricao = "Revisão",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
            )
        val pecaServico = PecaServico.criar(peca, BigDecimal("1.5"))

        val comPeca = ordemServico.adicionarPeca(pecaServico)

        assertTrue(ordemServico.pecas.isEmpty())
        assertEquals(listOf(pecaServico), comPeca.pecas)
    }

    @Test
    fun `deve alterar status preservando imutabilidade`() {
        val ordemServico =
            OrdemServico.criar(
                descricao = "Revisão",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
            )

        val emDiagnostico =
            ordemServico.alterarStatus(OrdemServicoStatus.EM_DIAGNOSTICO)

        assertEquals(OrdemServicoStatus.RECEBIDA, ordemServico.status)
        assertEquals(OrdemServicoStatus.EM_DIAGNOSTICO, emDiagnostico.status)
    }

    @Test
    fun `deve registrar dataInicioExecucao ao transitar para EM_EXECUCAO`() {
        val agora = Instant.now()
        val ordemServico =
            OrdemServico.criar(
                "Revisão",
                funcionario,
                cliente,
                veiculo,
                OrdemServicoStatus.AGUARDANDO_APROVACAO)

        val emExecucao =
            ordemServico.alterarStatus(
                OrdemServicoStatus.EM_EXECUCAO,
                agora
            )

        assertEquals(agora, emExecucao.dataInicioExecucao)
        assertNull(emExecucao.dataFinalizacao)
    }

    @Test
    fun `deve registrar dataFinalizacao ao transitar para FINALIZADA`() {
        val agora = Instant.now()
        val ordemServico =
            OrdemServico.criar(
                "Revisão",
                funcionario,
                cliente,
                veiculo,
                OrdemServicoStatus.EM_EXECUCAO,)

        val finalizada =
            ordemServico.alterarStatus(
                OrdemServicoStatus.FINALIZADA,
                agora
            )

        assertEquals(agora, finalizada.dataFinalizacao)
        assertNull(finalizada.dataInicioExecucao)
    }

    @ParameterizedTest
    @CsvSource(
        "RECEBIDA, EM_DIAGNOSTICO",
        "EM_DIAGNOSTICO, AGUARDANDO_APROVACAO",
        "AGUARDANDO_APROVACAO, EM_EXECUCAO",
        "EM_EXECUCAO, FINALIZADA",
        "FINALIZADA, ENTREGUE",
    )
    fun `deve avancar pelo fluxo principal` (
        atual: OrdemServicoStatus,
        esperado: OrdemServicoStatus,
    ) {
        val ordemServico =
            OrdemServico.criar(
                descricao = "Revisão",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
                status = atual,
            )

        val avancada = ordemServico.avancarStatus()

        assertEquals(esperado, avancada.status)
        assertEquals(atual, ordemServico.status)
    }

    @Test
    fun `deve permitir cancelamento enquanto aguarda aprovacao`() {
        val ordemServico =
            OrdemServico.criar(
                descricao = "Revisão",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
                status = OrdemServicoStatus.AGUARDANDO_APROVACAO,
            )

        val cancelada = ordemServico.alterarStatus(OrdemServicoStatus.CANCELADA)

        assertEquals(OrdemServicoStatus.CANCELADA, cancelada.status)
        assertEquals(OrdemServicoStatus.AGUARDANDO_APROVACAO, ordemServico.status)
    }

    @ParameterizedTest
    @CsvSource(
        "RECEBIDA, EM_EXECUCAO",
        "RECEBIDA, CANCELADA",
        "AGUARDANDO_APROVACAO, FINALIZADA",
        "EM_EXECUCAO, CANCELADA",
        "ENTREGUE, CANCELADA",
    )
    fun `deve rejeitar transicoes invalidas`(
        atual: OrdemServicoStatus,
        esperado: OrdemServicoStatus,
    ) {
        val ordemServico =
            OrdemServico.criar(
                descricao = "Revisão",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
                status = atual,
            )

        assertThrows(IllegalStateException::class.java) {
            ordemServico.alterarStatus(esperado)
        }

        assertEquals(atual, ordemServico.status)
    }

    @ParameterizedTest
    @EnumSource(
        value = OrdemServicoStatus::class,
        names = ["ENTREGUE", "CANCELADA"],
    )
    fun `nao deve avancar a partir do estado final`(
        statusFinal: OrdemServicoStatus,
    ) {
        val ordemServico =
            OrdemServico.criar(
                descricao = "Revisão",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
                status = statusFinal,
            )

        assertThrows(IllegalStateException::class.java) {
            ordemServico.avancarStatus()
        }

        assertEquals(statusFinal, ordemServico.status)
    }

    @Test
    fun `deve preservar timestamps ao transitar para outros status`() {
        val inicio = Instant.parse("2026-08-16T20:00:00Z")
        val fim = Instant.parse("2026-08-16T21:00:00Z")

        val finalizada =
            OrdemServico
                .criar(
                    descricao = "Revisão",
                    funcionario = funcionario,
                    cliente = cliente,
                    veiculo = veiculo,
                    status = OrdemServicoStatus.AGUARDANDO_APROVACAO,
                ).alterarStatus(OrdemServicoStatus.EM_EXECUCAO, inicio)
                .alterarStatus(OrdemServicoStatus.FINALIZADA, fim)

        val entregue =
            finalizada.alterarStatus(OrdemServicoStatus.ENTREGUE)

        assertEquals(inicio, entregue.dataInicioExecucao)
        assertEquals(fim, entregue.dataFinalizacao)
    }

    @Test
    fun `criar deve registrar dataAbertura`() {
        val antes = Instant.now()
        val ordemServico = OrdemServico.criar("Revisão", funcionario, cliente, veiculo)
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
            OrdemServico.criar(
                descricao = "Revisão",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
                pecas =
                    listOf(
                        PecaServico.criar(peca, BigDecimal("2")), // 10 * 2 = 20
                        PecaServico.criar(outraPeca, BigDecimal("1.5")), // 30 * 1.5 = 45.0
                    ),
            )

        val orcamento = ordemServico.gerarOrcamento()

        assertEquals(ordemServico.id, orcamento.ordemServicoId)
        assertEquals(2, orcamento.itens.size)
        assertEquals(0, BigDecimal("65.0").compareTo(orcamento.valorTotal))

        val itemFiltro = orcamento.itens.first { it.codigoReferencia == "PEC001" }
        assertEquals("Filtro", itemFiltro.descricao)
        assertEquals(BigDecimal.TEN, itemFiltro.valorUnitario)
        assertEquals(BigDecimal("2"), itemFiltro.quantidade)
        assertEquals(0, BigDecimal("20").compareTo(itemFiltro.subtotal))
    }

    @Test
    fun `deve gerar orcamento zerado para servico sem pecas`() {
        val ordemServico =
            OrdemServico.criar(
                descricao = "Diagnóstico",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
            )

        val orcamento = ordemServico.gerarOrcamento()

        assertTrue(orcamento.itens.isEmpty())
        assertEquals(0, BigDecimal.ZERO.compareTo(orcamento.valorTotal))
    }
}
