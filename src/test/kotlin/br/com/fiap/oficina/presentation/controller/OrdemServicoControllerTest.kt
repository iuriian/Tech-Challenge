package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.dto.ItemOrcamentoResponse
import br.com.fiap.oficina.application.dto.OrcamentoResponse
import br.com.fiap.oficina.application.dto.OrdemServicoResponse
import br.com.fiap.oficina.application.service.OrdemServicoService
import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.domain.enum.TipoItemOrcamento
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@WebMvcTest(OrdemServicoController::class)
@ActiveProfiles("test")
class OrdemServicoControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var service: OrdemServicoService

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve buscar ordem de servico por id`() {
        val id = UUID.randomUUID()

        `when`(
            service.listarPorId(id.toString()),
        ).thenReturn(
            response(id),
        )

        mockMvc
            .perform(
                get("/servicos/$id"),
            ).andExpect(
                status().isOk,
            )
    }

    @Test
    @WithMockUser(roles = ["CLIENTE"])
    fun `cliente pode consultar ordem de servico por id`() {
        val id = UUID.randomUUID()

        `when`(
            service.listarPorId(id.toString()),
        ).thenReturn(
            response(id),
        )

        mockMvc
            .perform(
                get("/servicos/$id"),
            ).andExpect(
                status().isOk,
            )
    }

    @Test
    @WithMockUser(roles = ["CLIENTE"])
    fun `cliente pode consultar orcamento da ordem de servico`() {
        val id = UUID.randomUUID()

        `when`(
            service.obterOrcamento(id.toString()),
        ).thenReturn(
            orcamentoResponse(),
        )

        mockMvc
            .perform(
                get("/servicos/$id/orcamento"),
            ).andExpect(
                status().isOk,
            )
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `atendente pode avancar status da OS`() {
        val id = UUID.randomUUID()

        `when`(
            service.avancarStatus(id.toString()),
        ).thenReturn(
            response(
                id = id,
                status = OrdemServicoStatus.EM_DIAGNOSTICO,
            ),
        )

        mockMvc
            .perform(
                patch("/servicos/$id/avancar")
                    .with(csrf()),
            ).andExpect(
                status().isOk,
            )
    }

    @Test
    @WithMockUser(roles = ["CLIENTE"])
    fun `cliente pode alterar status para cancelada via endpoint de status`() {
        val id = UUID.randomUUID()

        `when`(
            service.alterarStatus(
                id.toString(),
                OrdemServicoStatus.CANCELADA,
            ),
        ).thenReturn(
            response(
                id = id,
                status = OrdemServicoStatus.CANCELADA,
            ),
        )

        mockMvc
            .perform(
                patch("/servicos/$id/status")
                    .with(csrf())
                    .contentType(
                        MediaType.APPLICATION_JSON,
                    ).content(
                        """{"status":"CANCELADA"}""",
                    ),
            ).andExpect(
                status().isOk,
            )
    }

    private fun response(id: UUID, status: OrdemServicoStatus = OrdemServicoStatus.RECEBIDA): OrdemServicoResponse =
        OrdemServicoResponse(
            id = id,
            descricao = "Revisão Geral",
            status = status,
            funcionarioId = UUID.randomUUID().toString(),
            clienteId = UUID.randomUUID().toString(),
            veiculoId = UUID.randomUUID().toString(),
            itens = emptyList(),
            dataAbertura = Instant.now(),
            dataInicioExecucao = null,
            dataFinalizacao = null,
        )

    private fun orcamentoResponse(): OrcamentoResponse {
        val referenciaId = UUID.randomUUID()

        return OrcamentoResponse(
            itens =
            listOf(
                ItemOrcamentoResponse(
                    tipo = TipoItemOrcamento.PECA,
                    referenciaId = referenciaId,
                    codigoReferencia = "PEC001",
                    descricao = "Filtro",
                    valorUnitario = BigDecimal.TEN,
                    quantidade = BigDecimal.ONE,
                    subtotal = BigDecimal.TEN,
                ),
            ),
            valorTotal = BigDecimal.TEN,
        )
    }
}
