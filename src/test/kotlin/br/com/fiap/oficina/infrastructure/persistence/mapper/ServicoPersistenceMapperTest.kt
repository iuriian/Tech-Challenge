package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.entity.PecaServico
import br.com.fiap.oficina.domain.entity.OrdemServico
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.enum.ServicoStatus
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.enum.Cargo
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ServicoPersistenceMapperTest {
    private val clienteMapper = ClientePersistenceMapper()
    private val mapper =
        ServicoPersistenceMapper(
            clienteMapper,
            VeiculoPersistenceMapper(clienteMapper),
            PecaPersistenceMapper(),
        )

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

    @Test
    fun `deve fazer round-trip de servico com pecas`() {
        val ordemServico =
            OrdemServico(
                id = Id.generate(),
                descricao = "Troca de óleo",
                status = ServicoStatus.EM_EXECUCAO,
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
                pecas =
                    listOf(
                        PecaServico(
                            Peca(Id.generate(), "PEC001", "Filtro", precoDeVenda = BigDecimal.TEN),
                            BigDecimal("2"),
                        ),
                    ),
            )

        val jpa = mapper.toJpa(ordemServico)

        assertEquals(ordemServico.id.valor, jpa.id)
        assertEquals(ServicoStatus.EM_EXECUCAO, jpa.status)
        assertEquals(funcionario.id.valor, jpa.funcionario.id)
        assertEquals(BigDecimal("2"), jpa.pecas.first().quantidade)
        assertEquals(ordemServico, mapper.toDomain(jpa))
    }

    @Test
    fun `deve usar status padrao e funcionario zero quando jpa possui nulos`() {
        val ordemServico =
            OrdemServico(
                id = Id.generate(),
                descricao = "Revisão",
                status = ServicoStatus.RECEBIDA,
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
            )
        val jpa =
            mapper.toJpa(ordemServico).apply {
                status = null
            }

        val resultado = mapper.toDomain(jpa)

        assertEquals(ServicoStatus.RECEBIDA, resultado.status)
        assertEquals(funcionario.id.valor, resultado.funcionario.id.valor)
    }
}
