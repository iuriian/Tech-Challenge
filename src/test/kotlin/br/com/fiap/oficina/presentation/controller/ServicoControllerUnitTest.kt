package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.service.ServicoService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.enum.ServicoStatus
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.domain.valueobject.ItemOrcamento
import br.com.fiap.oficina.domain.valueobject.Orcamento
import br.com.fiap.oficina.presentation.dto.ServicoDto
import br.com.fiap.oficina.presentation.mapper.ServicoMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.util.UUID

class ServicoControllerUnitTest {

    private val service = mock(ServicoService::class.java)
    private val controller = ServicoController(service, ServicoMapper())

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

    private val servico = Servico(
        id = Id.gerar(),
        descricao = "Troca de óleo",
        status = ServicoStatus.RECEBIDA,
        funcionarioId = 1L,
        cliente = cliente,
        veiculo = veiculo
    )

    private fun servicoDto() = ServicoDto(
        descricao = "Troca de óleo",
        funcionarioId = 1L,
        clienteId = cliente.id.valor,
        veiculoId = veiculo.id.valor
    )

    @Test
    fun `criar deve retornar dto do servico salvo`() {
        `when`(service.salvar(anyObject())).thenReturn(servico)

        val dto = controller.criar(servicoDto())

        assertEquals(servico.id.valor, dto.id)
        assertEquals("Troca de óleo", dto.descricao)
    }

    @Test
    fun `atualizar deve retornar dto do servico salvo`() {
        `when`(service.salvar(anyObject())).thenReturn(servico)

        val dto = controller.atualizar(UUID.randomUUID(), servicoDto())

        assertEquals(servico.id.valor, dto.id)
    }

    @Test
    fun `listarTodos deve mapear lista`() {
        `when`(service.listarTodos()).thenReturn(listOf(servico))

        assertEquals(1, controller.listarTodos().size)
    }

    @Test
    fun `obterOrcamento deve retornar dto do orcamento`() {
        val id = UUID.randomUUID()
        val orcamento = Orcamento(
            servicoId = Id.from(id),
            itens = listOf(
                ItemOrcamento(
                    pecaId = UUID.randomUUID().let { Id.from(it) },
                    codigo = "PEC001",
                    nome = "Filtro",
                    precoUnitario = BigDecimal.TEN,
                    quantidade = BigDecimal("2"),
                    subtotal = BigDecimal("20")
                )
            ),
            valorTotal = BigDecimal("20")
        )
        `when`(service.obterOrcamento(Id.from(id))).thenReturn(orcamento)

        val dto = controller.obterOrcamento(id)

        assertEquals(id, dto.servicoId)
        assertEquals(BigDecimal("20"), dto.valorTotal)
        assertEquals(1, dto.itens.size)
    }

    @Test
    fun `obterOrcamento deve retornar 404 quando servico nao existe`() {
        val id = UUID.randomUUID()
        `when`(service.obterOrcamento(Id.from(id)))
            .thenThrow(IllegalArgumentException("Serviço não encontrado com o ID: $id"))

        val exception = assertThrows(ResponseStatusException::class.java) {
            controller.obterOrcamento(id)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
    }

    @Test
    fun `deletarPorId deve delegar ao service`() {
        val id = UUID.randomUUID()
        `when`(service.deletarPorId(Id.from(id))).thenReturn("Servico deletado.")

        controller.deletarPorId(id)

        verify(service).deletarPorId(Id.from(id))
    }
}
