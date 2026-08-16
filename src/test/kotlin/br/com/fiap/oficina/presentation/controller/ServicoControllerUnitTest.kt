package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.service.ServicoService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.entity.OrdemServico
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.enum.Cargo
import br.com.fiap.oficina.domain.enum.ServicoStatus
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.domain.valueobject.ItemOrcamento
import br.com.fiap.oficina.domain.valueobject.Orcamento
import br.com.fiap.oficina.presentation.dto.AlterarStatusDto
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

    private val ordemServico =
        OrdemServico(
            id = Id.generate(),
            descricao = "Troca de óleo",
            status = ServicoStatus.RECEBIDA,
            funcionario = funcionario,
            cliente = cliente,
            veiculo = veiculo,
        )

    private fun servicoDto() =
        ServicoDto(
            descricao = "Troca de óleo",
            funcionarioId = funcionario.id.valor.toString(),
            clienteId = cliente.id.valor.toString(),
            veiculoId = veiculo.id.valor.toString(),
        )

    @Test
    fun `criar deve retornar dto do servico salvo`() {
        `when`(service.salvar(anyObject())).thenReturn(ordemServico)

        val dto = controller.criar(servicoDto())

        assertEquals(ordemServico.id.valor, dto.id)
        assertEquals("Troca de óleo", dto.descricao)
    }

    @Test
    fun `atualizar deve retornar dto do servico salvo`() {
        `when`(service.salvar(anyObject())).thenReturn(ordemServico)

        val dto = controller.atualizar("00000000-0000-0000-0000-000000000001", servicoDto())

        assertEquals(ordemServico.id.valor, dto.id)
    }

    @Test
    fun `listarTodos deve mapear lista`() {
        `when`(service.listarTodos()).thenReturn(listOf(ordemServico))

        assertEquals(1, controller.listarTodos().size)
    }

    @Test
    fun `listarPorCliente deve mapear lista de servicos do cliente`() {
        val clienteId = cliente.id.valor
        `when`(service.listarPorCliente(Id.fromString(clienteId.toString()))).thenReturn(listOf(ordemServico))

        val resultado = controller.listarPorCliente(clienteId.toString())

        assertEquals(1, resultado.size)
        assertEquals(clienteId.toString(), resultado.first().clienteId)
    }

    @Test
    fun `listarPorCliente deve retornar lista vazia quando cliente nao tem servicos`() {
        val clienteId = UUID.randomUUID()
        `when`(service.listarPorCliente(Id.fromString(clienteId.toString()))).thenReturn(emptyList())

        assertTrue(controller.listarPorCliente(clienteId.toString()).isEmpty())
    }

    @Test
    fun `obterOrcamento deve retornar dto do orcamento`() {
        val id = Id.fromString("00000000-0000-0000-0000-000000000001")
        val orcamento =
            Orcamento(
                servicoId = Id.fromString("00000000-0000-0000-0000-000000000001"),
                itens =
                    listOf(
                        ItemOrcamento(
                            pecaId = UUID.randomUUID().let { Id.fromString("00000000-0000-0000-0000-000000000002") },
                            codigo = "PEC001",
                            nome = "Filtro",
                            precoUnitario = BigDecimal.TEN,
                            quantidade = BigDecimal("2"),
                            subtotal = BigDecimal("20"),
                        ),
                    ),
                valorTotal = BigDecimal("20"),
            )
        `when`(service.obterOrcamento(Id(id.valor))).thenReturn(orcamento)

        val dto = controller.obterOrcamento(id.valor.toString())

        assertEquals(id.valor, dto.servicoId)
        assertEquals(BigDecimal("20"), dto.valorTotal)
        assertEquals(1, dto.itens.size)
    }

    @Test
    fun `obterOrcamento deve retornar 404 quando servico nao existe`() {
        val id = UUID.randomUUID()
        `when`(service.obterOrcamento(Id.fromString(id.toString())))
            .thenThrow(IllegalArgumentException("Serviço não encontrado com o ID: $id"))

        val exception =
            assertThrows(ResponseStatusException::class.java) {
                controller.obterOrcamento(id.toString())
            }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
    }

    @Test
    fun `deletarPorId deve delegar ao service`() {
        val id = UUID.randomUUID()
        `when`(service.deletarPorId(Id.fromString(id.toString()))).thenReturn("Servico deletado.")

        controller.deletarPorId(id.toString())

        verify(service).deletarPorId(Id.fromString(id.toString()))
    }

    @Test
    fun `avancarStatus deve retornar dto com novo status`() {
        val id = ordemServico.id.valor
        val servicoAvancado = ordemServico.copy(status = ServicoStatus.EM_DIAGNOSTICO)
        `when`(service.avancarStatus(Id.fromString(id.toString()))).thenReturn(servicoAvancado)

        val dto = controller.avancarStatus(id.toString())

        assertEquals(ServicoStatus.EM_DIAGNOSTICO, dto.status)
    }

    @Test
    fun `avancarStatus deve retornar 404 quando servico nao existe`() {
        val id = UUID.randomUUID()
        `when`(service.avancarStatus(Id.fromString(id.toString())))
            .thenThrow(IllegalArgumentException("Serviço não encontrado com o ID: $id"))

        val exception =
            assertThrows(ResponseStatusException::class.java) {
                controller.avancarStatus(id.toString())
            }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
    }

    @Test
    fun `avancarStatus deve retornar 422 quando status e final`() {
        val id = UUID.randomUUID()
        `when`(service.avancarStatus(Id.fromString(id.toString())))
            .thenThrow(IllegalStateException("Serviço no status 'ENTREGUE' é um estado final e não pode avançar."))

        val exception =
            assertThrows(ResponseStatusException::class.java) {
                controller.avancarStatus(id.toString())
            }

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.statusCode)
    }

    @Test
    fun `alterarStatus deve retornar dto com status alterado`() {
        val id = ordemServico.id.valor
        val servicoCancelado = ordemServico.copy(status = ServicoStatus.CANCELADA)
        `when`(service.alterarStatus(Id.fromString(id.toString()), ServicoStatus.CANCELADA)).thenReturn(servicoCancelado)

        val dto = controller.alterarStatus(id.toString(), AlterarStatusDto(ServicoStatus.CANCELADA))

        assertEquals(ServicoStatus.CANCELADA, dto.status)
    }

    @Test
    fun `alterarStatus deve retornar 404 quando servico nao existe`() {
        val id = UUID.randomUUID()
        `when`(service.alterarStatus(Id.fromString(id.toString()), ServicoStatus.CANCELADA))
            .thenThrow(IllegalArgumentException("Serviço não encontrado com o ID: $id"))

        val exception =
            assertThrows(ResponseStatusException::class.java) {
                controller.alterarStatus(id.toString(), AlterarStatusDto(ServicoStatus.CANCELADA))
            }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
    }

    @Test
    fun `alterarStatus deve retornar 422 para transicao invalida`() {
        val id = UUID.randomUUID()
        `when`(service.alterarStatus(Id.fromString(id.toString()), ServicoStatus.ENTREGUE))
            .thenThrow(IllegalStateException("Transição inválida de 'RECEBIDA' para 'ENTREGUE'."))

        val exception =
            assertThrows(ResponseStatusException::class.java) {
                controller.alterarStatus(id.toString(), AlterarStatusDto(ServicoStatus.ENTREGUE))
            }

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.statusCode)
    }
}
