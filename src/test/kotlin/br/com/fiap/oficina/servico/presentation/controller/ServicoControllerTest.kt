package br.com.fiap.oficina.servico.presentation.controller

import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.servico.application.services.ServicoService
import br.com.fiap.oficina.servico.domain.entities.OrdemServico
import br.com.fiap.oficina.servico.domain.enums.OrdemServicoStatus
import br.com.fiap.oficina.servico.domain.enums.TipoItemOrcamento
import br.com.fiap.oficina.servico.domain.valueobjects.ItemOrcamento
import br.com.fiap.oficina.servico.domain.valueobjects.Orcamento
import br.com.fiap.oficina.servico.presentation.dto.ServicoDto
import br.com.fiap.oficina.servico.presentation.mapper.ServicoMapper
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
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var service: ServicoService

    @MockitoBean
    lateinit var mapper: ServicoMapper

    private val funcionarioId = Id.generate()
    private val clienteId = Id.generate()
    private val veiculoId = Id.generate()

    private fun buildOrdemServico(id: UUID) = OrdemServico(
        id = Id.fromString(id.toString()),
        descricao = "Revisao Geral",
        status = OrdemServicoStatus.RECEBIDA,
        funcionarioId = funcionarioId,
        clienteId = clienteId,
        veiculoId = veiculoId,
        pecas = emptyList(),
    )

    @Test
    @WithMockUser
    fun `deve buscar ordem de servico por id`() {
        val id = UUID.randomUUID()
        `when`(service.listarPorId(Id.fromString(id.toString()))).thenReturn(buildOrdemServico(id))

        mockMvc.perform(get("/servicos/$id")).andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["CLIENTE"])
    fun `cliente pode consultar ordem de servico por id`() {
        val id = UUID.randomUUID()
        val ordemServico = buildOrdemServico(id)
        `when`(service.listarPorId(Id.fromString(id.toString()))).thenReturn(ordemServico)
        `when`(mapper.toResponse(ordemServico)).thenReturn(
            ServicoDto(
                id = id,
                descricao = ordemServico.descricao,
                status = ordemServico.status,
                funcionarioId =
                ordemServico.funcionarioId.valor
                    .toString(),
                clienteId = clienteId.valor.toString(),
                veiculoId = veiculoId.valor.toString(),
            ),
        )

        mockMvc.perform(get("/servicos/$id")).andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["CLIENTE"])
    fun `cliente pode consultar orcamento da ordem de servico`() {
        val id = UUID.randomUUID()
        val orcamento =
            Orcamento(
                ordemServicoId = Id.fromString("00000000-0000-0000-0000-000000000001"),
                itens =
                listOf(
                    ItemOrcamento(
                        tipo = TipoItemOrcamento.PECA,
                        referenciaId = Id.generate(),
                        descricao = "Filtro",
                        valorUnitario = BigDecimal.TEN,
                        quantidade = BigDecimal.ONE,
                        codigoReferencia = "PEC001",
                    ),
                ),
            )
        `when`(service.obterOrcamento(Id.fromString(id.toString()))).thenReturn(orcamento)

        mockMvc.perform(get("/servicos/$id/orcamento")).andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `atendente pode avancar status da OS`() {
        val id = UUID.randomUUID()
        val ordemServicoAvancada = buildOrdemServico(id).copy(status = OrdemServicoStatus.EM_DIAGNOSTICO)
        `when`(service.avancarStatus(Id.fromString(id.toString()))).thenReturn(ordemServicoAvancada)
        `when`(mapper.toResponse(ordemServicoAvancada)).thenReturn(
            ServicoDto(
                id = id,
                descricao = ordemServicoAvancada.descricao,
                status = ordemServicoAvancada.status,
                funcionarioId =
                ordemServicoAvancada.funcionarioId.valor.toString(),
                clienteId = clienteId.valor.toString(),
                veiculoId = veiculoId.valor.toString(),
            ),
        )

        mockMvc.perform(patch("/servicos/$id/avancar").with(csrf())).andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["CLIENTE"])
    fun `cliente pode alterar status para cancelada via endpoint de status`() {
        val id = UUID.randomUUID()
        val ordemServicoCancelada = buildOrdemServico(id).copy(status = OrdemServicoStatus.CANCELADA)
        `when`(
            service.alterarStatus(Id.fromString(id.toString()), OrdemServicoStatus.CANCELADA),
        ).thenReturn(ordemServicoCancelada)
        `when`(mapper.toResponse(ordemServicoCancelada)).thenReturn(
            ServicoDto(
                id = id,
                descricao = ordemServicoCancelada.descricao,
                status = ordemServicoCancelada.status,
                funcionarioId =
                ordemServicoCancelada.funcionarioId.valor
                    .toString(),
                clienteId = clienteId.valor.toString(),
                veiculoId = veiculoId.valor.toString(),
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
