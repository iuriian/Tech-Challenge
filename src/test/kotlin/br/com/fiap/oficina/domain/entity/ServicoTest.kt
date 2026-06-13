package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.enum.ServicoStatus
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ServicoTest {

    private val cliente = Cliente(
        id = Id.gerar(),
        nome = "Cliente",
        documento = Documento.cpf("39053344705"),
        email = "cliente@example.com"
    )

    private val veiculo = Veiculo(
        id = Id.gerar(),
        marca = "Volkswagen",
        nome = "Gol",
        modelo = "Gol 1.6",
        ano = "2020",
        placa = "ABC1D23",
        motorista = cliente
    )

    private val peca = Peca(
        id = Id.gerar(),
        codigo = "PEC001",
        nome = "Filtro",
        precoDeVenda = BigDecimal.TEN
    )

    @Test
    fun `deve criar servico valido com status padrao`() {
        val servico = Servico.criar(
            descricao = "Troca de óleo",
            funcionarioId = 1L,
            cliente = cliente,
            veiculo = veiculo
        )

        assertNotNull(servico.id)
        assertEquals("Troca de óleo", servico.descricao)
        assertEquals(ServicoStatus.RECEBIDA, servico.status)
        assertEquals(1L, servico.funcionarioId)
        assertEquals(cliente, servico.cliente)
        assertEquals(veiculo, servico.veiculo)
        assertTrue(servico.pecas.isEmpty())
    }

    @Test
    fun `deve criar servico com status e pecas informados`() {
        val servico = Servico.criar(
            descricao = "Troca de óleo",
            funcionarioId = 1L,
            cliente = cliente,
            veiculo = veiculo,
            status = ServicoStatus.EM_EXECUCAO,
            pecas = listOf(peca)
        )

        assertEquals(ServicoStatus.EM_EXECUCAO, servico.status)
        assertEquals(listOf(peca), servico.pecas)
    }

    @Test
    fun `deve rejeitar descricao em branco`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Servico.criar(
                descricao = "",
                funcionarioId = 1L,
                cliente = cliente,
                veiculo = veiculo
            )
        }

        assertEquals("Descrição do serviço é obrigatória", exception.message)
    }

    @Test
    fun `deve adicionar peca preservando imutabilidade`() {
        val servico = Servico.criar(
            descricao = "Revisão",
            funcionarioId = 1L,
            cliente = cliente,
            veiculo = veiculo
        )

        val comPeca = servico.adicionarPeca(peca)

        assertTrue(servico.pecas.isEmpty())
        assertEquals(listOf(peca), comPeca.pecas)
    }

    @Test
    fun `deve alterar status preservando imutabilidade`() {
        val servico = Servico.criar(
            descricao = "Revisão",
            funcionarioId = 1L,
            cliente = cliente,
            veiculo = veiculo
        )

        val finalizado = servico.alterarStatus(ServicoStatus.FINALIZADA)

        assertEquals(ServicoStatus.RECEBIDA, servico.status)
        assertEquals(ServicoStatus.FINALIZADA, finalizado.status)
    }
}
