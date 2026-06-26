package br.com.fiap.oficina.presentation.controller

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
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.util.UUID

@WebMvcTest(ServicoController::class)
class ServicoControllerTest {
    @Autowired lateinit var mockMvc: MockMvc

    @MockitoBean lateinit var service: ServicoService

    @MockitoBean lateinit var mapper: ServicoMapper

    private val cliente =
        Cliente(
            id = Id.generate(),
            nome = "Cliente Teste",
            documento = Documento.cpf("39053344705"),
            email = "cliente@teste.com",
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

    private fun buildServico(id: UUID) =
        Servico(
            id = Id.fromString(id),
            descricao = "Revisao Geral",
            status = ServicoStatus.RECEBIDA,
            funcionarioId = 1L,
            cliente = cliente,
            veiculo = veiculo,
            pecas = emptyList(),
        )

    @Test
    @WithMockUser
    fun `deve buscar servico por id`() {
        val id = UUID.randomUUID()
        `when`(service.listarPorId(Id.fromString(id))).thenReturn(buildServico(id))

        mockMvc.perform(get("/servicos/$id")).andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["CLIENTE"])
    fun `cliente pode consultar servico por id`() {
        val id = UUID.randomUUID()
        val servico = buildServico(id)
        `when`(service.listarPorId(Id.fromString(id))).thenReturn(servico)
        `when`(mapper.toResponse(servico)).thenReturn(
            ServicoDto(
                id = id,
                descricao = servico.descricao,
                status = servico.status,
                funcionarioId = servico.funcionarioId,
                clienteId = cliente.id.valor,
                veiculoId = veiculo.id.valor,
            ),
        )

        mockMvc.perform(get("/servicos/$id")).andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["CLIENTE"])
    fun `cliente pode consultar orcamento do servico`() {
        val id = UUID.randomUUID()
        val orcamento =
            Orcamento(
                servicoId = Id.fromString(id),
                itens =
                    listOf(
                        ItemOrcamento(
                            pecaId = Id.generate(),
                            codigo = "PEC001",
                            nome = "Filtro",
                            precoUnitario = BigDecimal.TEN,
                            quantidade = BigDecimal.ONE,
                            subtotal = BigDecimal.TEN,
                        ),
                    ),
                valorTotal = BigDecimal.TEN,
            )
        `when`(service.obterOrcamento(Id.fromString(id))).thenReturn(orcamento)

        mockMvc.perform(get("/servicos/$id/orcamento")).andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `atendente pode avancar status da OS`() {
        val id = UUID.randomUUID()
        val servicoAvancado = buildServico(id).copy(status = ServicoStatus.EM_DIAGNOSTICO)
        `when`(service.avancarStatus(Id.fromString(id))).thenReturn(servicoAvancado)
        `when`(mapper.toResponse(servicoAvancado)).thenReturn(
            ServicoDto(
                id = id,
                descricao = servicoAvancado.descricao,
                status = servicoAvancado.status,
                funcionarioId = servicoAvancado.funcionarioId,
                clienteId = cliente.id.valor,
                veiculoId = veiculo.id.valor,
            ),
        )

        mockMvc.perform(patch("/servicos/$id/avancar").with(csrf())).andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["CLIENTE"])
    fun `cliente pode alterar status para cancelada via endpoint de status`() {
        val id = UUID.randomUUID()
        val servicoCancelado = buildServico(id).copy(status = ServicoStatus.CANCELADA)
        `when`(service.alterarStatus(Id.fromString(id), ServicoStatus.CANCELADA)).thenReturn(servicoCancelado)
        `when`(mapper.toResponse(servicoCancelado)).thenReturn(
            ServicoDto(
                id = id,
                descricao = servicoCancelado.descricao,
                status = servicoCancelado.status,
                funcionarioId = servicoCancelado.funcionarioId,
                clienteId = cliente.id.valor,
                veiculoId = veiculo.id.valor,
            ),
        )

        mockMvc
            .perform(
                patch("/servicos/$id/status")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"status": "CANCELADA"}"""),
            ).andExpect(status().isOk)
    }
}
