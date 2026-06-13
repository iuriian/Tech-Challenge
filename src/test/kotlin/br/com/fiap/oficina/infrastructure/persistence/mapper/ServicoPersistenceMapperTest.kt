package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.enum.ServicoStatus
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ServicoPersistenceMapperTest {

    private val clienteMapper = ClientePersistenceMapper()
    private val mapper = ServicoPersistenceMapper(
        clienteMapper,
        VeiculoPersistenceMapper(clienteMapper),
        PecaPersistenceMapper()
    )

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

    @Test
    fun `deve fazer round-trip de servico com pecas`() {
        val servico = Servico(
            id = Id.gerar(),
            descricao = "Troca de óleo",
            status = ServicoStatus.EM_EXECUCAO,
            funcionarioId = 7L,
            cliente = cliente,
            veiculo = veiculo,
            pecas = listOf(
                Peca(Id.gerar(), "PEC001", "Filtro", precoDeVenda = BigDecimal.TEN)
            )
        )

        val jpa = mapper.toJpa(servico)

        assertEquals(servico.id.valor, jpa.id)
        assertEquals(ServicoStatus.EM_EXECUCAO, jpa.status)
        assertEquals(7L, jpa.funcionarioId)
        assertEquals(servico, mapper.toDomain(jpa))
    }

    @Test
    fun `deve usar status padrao e funcionario zero quando jpa possui nulos`() {
        val servico = Servico(
            id = Id.gerar(),
            descricao = "Revisão",
            status = ServicoStatus.RECEBIDA,
            funcionarioId = 1L,
            cliente = cliente,
            veiculo = veiculo
        )
        val jpa = mapper.toJpa(servico).apply {
            status = null
            funcionarioId = null
        }

        val resultado = mapper.toDomain(jpa)

        assertEquals(ServicoStatus.RECEBIDA, resultado.status)
        assertEquals(0L, resultado.funcionarioId)
    }
}
