package br.com.fiap.oficina.presentation.mapper

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

class ServicoMapperTest {

    private val mapper = ServicoMapper()

    @Test
    fun `deve mapear Servico para ServicoDto`() {
        val cliente = Cliente(Id.gerar(), "Cliente", Documento.cpf("39053344705"), "c@e.com")
        val veiculo = Veiculo(Id.gerar(), "VW", "Gol", "Gol 1.6", "2020", "ABC1D23", cliente)
        val peca = Peca(Id.gerar(), "PEC001", "Filtro", precoDeVenda = BigDecimal.TEN)
        val servico = Servico(
            id = Id.gerar(),
            descricao = "Troca de óleo",
            status = ServicoStatus.EM_EXECUCAO,
            funcionarioId = 3L,
            cliente = cliente,
            veiculo = veiculo,
            pecas = listOf(peca)
        )

        val dto = mapper.toResponse(servico)

        assertEquals(servico.id.valor, dto.id)
        assertEquals("Troca de óleo", dto.descricao)
        assertEquals(ServicoStatus.EM_EXECUCAO, dto.status)
        assertEquals(3L, dto.funcionarioId)
        assertEquals(cliente.id.valor, dto.clienteId)
        assertEquals(veiculo.id.valor, dto.veiculoId)
        assertEquals(listOf(peca.id.valor), dto.pecasIds)
    }
}
