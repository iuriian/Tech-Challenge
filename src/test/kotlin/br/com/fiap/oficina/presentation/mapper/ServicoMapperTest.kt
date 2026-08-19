package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.entity.PecaServico
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.enum.Cargo
import br.com.fiap.oficina.domain.enum.ServicoStatus
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.domain.valueobject.ItemOrcamento
import br.com.fiap.oficina.domain.valueobject.Orcamento
import br.com.fiap.oficina.presentation.dto.PecaServicoDto
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class ServicoMapperTest {
    private val mapper = ServicoMapper()

    @Test
    fun `deve mapear Servico para ServicoDto`() {
        val cliente = Cliente(Id.generate(), "Cliente", Documento.cpf("39053344705"), "c@e.com")
        val veiculo = Veiculo(Id.generate(), "VW", "Gol", "Gol 1.6", "2020", "ABC1D23", cliente)
        val funcionario = Funcionario(Id.generate(), "Funcionario", Cargo.MECANICO)
        val peca = Peca(Id.generate(), "PEC001", "Filtro", precoDeVenda = BigDecimal.TEN)
        val pecaServico = PecaServico.criar(peca, BigDecimal("2"))
        val servico =
            Servico(
                id = Id.generate(),
                descricao = "Troca de óleo",
                status = ServicoStatus.EM_EXECUCAO,
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
                pecas = listOf(pecaServico),
            )

        val dto = mapper.toResponse(servico)

        assertEquals(servico.id.valor, dto.id)
        assertEquals("Troca de óleo", dto.descricao)
        assertEquals(ServicoStatus.EM_EXECUCAO, dto.status)
        assertEquals(funcionario.id.valor.toString(), dto.funcionarioId)
        assertEquals(cliente.id.valor.toString(), dto.clienteId)
        assertEquals(veiculo.id.valor.toString(), dto.veiculoId)
        assertEquals(listOf(PecaServicoDto(peca.id.valor.toString(), BigDecimal("2"))), dto.pecas)
    }

    @Test
    fun `deve mapear Orcamento para OrcamentoDto`() {
        val servicoId = Id.generate()
        val pecaId = Id.generate()
        val orcamento =
            Orcamento(
                servicoId = servicoId,
                itens =
                    listOf(
                        ItemOrcamento(
                            pecaId = pecaId,
                            codigo = "PEC001",
                            nome = "Filtro",
                            precoUnitario = BigDecimal.TEN,
                            quantidade = BigDecimal("2"),
                            subtotal = BigDecimal("20"),
                        ),
                    ),
                valorTotal = BigDecimal("20"),
            )

        val dto = mapper.toResponse(orcamento)

        assertEquals(servicoId.valor, dto.servicoId)
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
