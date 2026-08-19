package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.enum.Cargo
import br.com.fiap.oficina.domain.enum.ServicoStatus
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant

class ServicoTest {
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
        val servico =
            Servico.criar(
                descricao = "Troca de óleo",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
            )

        assertNotNull(servico.id)
        assertEquals("Troca de óleo", servico.descricao)
        assertEquals(ServicoStatus.RECEBIDA, servico.status)
        assertEquals(funcionario, servico.funcionario)
        assertEquals(cliente, servico.cliente)
        assertEquals(veiculo, servico.veiculo)
        assertTrue(servico.pecas.isEmpty())
    }

    @Test
    fun `deve criar servico com status e pecas informados`() {
        val pecaServico = PecaServico.criar(peca, BigDecimal("2"))
        val servico =
            Servico.criar(
                descricao = "Troca de óleo",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
                status = ServicoStatus.EM_EXECUCAO,
                pecas = listOf(pecaServico),
            )

        assertEquals(ServicoStatus.EM_EXECUCAO, servico.status)
        assertEquals(listOf(pecaServico), servico.pecas)
    }

    @Test
    fun `deve rejeitar descricao em branco`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Servico.criar(
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
        val servico =
            Servico.criar(
                descricao = "Revisão",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
            )

        val comPeca = servico.adicionarPeca(peca, BigDecimal("3"))

        assertTrue(servico.pecas.isEmpty())
        assertEquals(listOf(PecaServico.criar(peca, BigDecimal("3"))), comPeca.pecas)
    }

    @Test
    fun `deve adicionar peca-servico diretamente preservando imutabilidade`() {
        val servico =
            Servico.criar(
                descricao = "Revisão",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
            )
        val pecaServico = PecaServico.criar(peca, BigDecimal("1.5"))

        val comPeca = servico.adicionarPeca(pecaServico)

        assertTrue(servico.pecas.isEmpty())
        assertEquals(listOf(pecaServico), comPeca.pecas)
    }

    @Test
    fun `deve alterar status preservando imutabilidade`() {
        val servico =
            Servico.criar(
                descricao = "Revisão",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
            )

        val finalizado = servico.alterarStatus(ServicoStatus.FINALIZADA)

        assertEquals(ServicoStatus.RECEBIDA, servico.status)
        assertEquals(ServicoStatus.FINALIZADA, finalizado.status)
    }

    @Test
    fun `deve registrar dataInicioExecucao ao transitar para EM_EXECUCAO`() {
        val agora = Instant.now()
        val servico = Servico.criar("Revisão", funcionario, cliente, veiculo)

        val emExecucao = servico.alterarStatus(ServicoStatus.EM_EXECUCAO, agora)

        assertEquals(agora, emExecucao.dataInicioExecucao)
        assertNull(emExecucao.dataFinalizacao)
    }

    @Test
    fun `deve registrar dataFinalizacao ao transitar para FINALIZADA`() {
        val agora = Instant.now()
        val servico = Servico.criar("Revisão", funcionario, cliente, veiculo)

        val finalizado = servico.alterarStatus(ServicoStatus.FINALIZADA, agora)

        assertEquals(agora, finalizado.dataFinalizacao)
        assertNull(finalizado.dataInicioExecucao)
    }

    @Test
    fun `deve preservar timestamps ao transitar para outros status`() {
        val inicio = Instant.now()
        val servico =
            Servico
                .criar("Revisão", funcionario, cliente, veiculo)
                .alterarStatus(ServicoStatus.EM_EXECUCAO, inicio)

        val entregue = servico.alterarStatus(ServicoStatus.ENTREGUE)

        assertEquals(inicio, entregue.dataInicioExecucao)
        assertNull(entregue.dataFinalizacao)
    }

    @Test
    fun `criar deve registrar dataAbertura`() {
        val antes = Instant.now()
        val servico = Servico.criar("Revisão", funcionario, cliente, veiculo)
        val depois = Instant.now()

        assertFalse(servico.dataAbertura.isBefore(antes))
        assertFalse(servico.dataAbertura.isAfter(depois))
        assertNull(servico.dataInicioExecucao)
        assertNull(servico.dataFinalizacao)
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
        val servico =
            Servico.criar(
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

        val orcamento = servico.gerarOrcamento()

        assertEquals(servico.id, orcamento.servicoId)
        assertEquals(2, orcamento.itens.size)
        assertEquals(0, BigDecimal("65.0").compareTo(orcamento.valorTotal))

        val itemFiltro = orcamento.itens.first { it.codigo == "PEC001" }
        assertEquals("Filtro", itemFiltro.nome)
        assertEquals(BigDecimal.TEN, itemFiltro.precoUnitario)
        assertEquals(BigDecimal("2"), itemFiltro.quantidade)
        assertEquals(0, BigDecimal("20").compareTo(itemFiltro.subtotal))
    }

    @Test
    fun `deve gerar orcamento zerado para servico sem pecas`() {
        val servico =
            Servico.criar(
                descricao = "Diagnóstico",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
            )

        val orcamento = servico.gerarOrcamento()

        assertTrue(orcamento.itens.isEmpty())
        assertEquals(0, BigDecimal.ZERO.compareTo(orcamento.valorTotal))
    }
}
