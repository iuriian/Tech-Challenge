package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.service.ServicoService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.entity.OrdemServico
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.enum.Cargo
import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.domain.enum.TipoItemOrcamento
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
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var service: ServicoService

    @MockitoBean
    lateinit var mapper: ServicoMapper

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

    private val funcionario =
        Funcionario(
            id = Id.generate(),
            nome = "Funcionario Teste",
            cargo = Cargo.MECANICO,
        )

    private fun buildOrdemServico(id: UUID) = OrdemServico(
        id = Id.fromString(id.toString()),
        descricao = "Revisao Geral",
        status = OrdemServicoStatus.RECEBIDA,
        funcionario = funcionario,
        cliente = cliente,
        veiculo = veiculo,
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
                    ordemServico.funcionario.id.valor
                        .toString(),
                clienteId = cliente.id.valor.toString(),
                veiculoId = veiculo.id.valor.toString(),
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
                    ordemServicoAvancada.funcionario.id.valor
                        .toString(),
                clienteId = cliente.id.valor.toString(),
                veiculoId = veiculo.id.valor.toString(),
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
                    ordemServicoCancelada.funcionario.id.valor
                        .toString(),
                clienteId = cliente.id.valor.toString(),
                veiculoId = veiculo.id.valor.toString(),
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
