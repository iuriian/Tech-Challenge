package br.com.fiap.oficina.integration

import br.com.fiap.oficina.domain.enum.ServicoStatus
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.PecaRepository
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.presentation.dto.AlterarStatusDto
import br.com.fiap.oficina.presentation.dto.PecaServicoDto
import br.com.fiap.oficina.presentation.dto.ServicoDto
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import java.math.BigDecimal
import java.util.UUID

/**
 * Testes de integração do fluxo de ordens de serviço, exercitando a máquina
 * de estados, o orçamento e as métricas contra o PostgreSQL real. Reutiliza
 * cliente (João/39053344705), veículo (ABC1D23) e peça (PEC001) do seed.
 */
class ServicoControllerIntegrationTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var clienteRepository: ClienteRepository

    @Autowired
    private lateinit var veiculoRepository: VeiculoRepository

    @Autowired
    private lateinit var pecaRepository: PecaRepository

    private fun clienteId(): UUID = clienteRepository.buscarPorDocumento("39053344705")!!.id.valor

    private fun veiculoId(): UUID = veiculoRepository.buscarPorPlaca("ABC1D23")!!.id.valor

    private fun pecaId(): UUID = pecaRepository.buscarAtivoPorCodigo("PEC001")!!.id.valor

    private fun servicoJson(descricao: String = "Revisão completa", quantidadePeca: BigDecimal = BigDecimal("2")) =
        objectMapper.writeValueAsString(
            ServicoDto(
                descricao = descricao,
                funcionarioId = "3f5f33b0-4f1f-4a76-9ef8-1dc8b8d1a1b3",
                clienteId = clienteId().toString(),
                veiculoId = veiculoId().toString(),
                pecas = listOf(PecaServicoDto(pecaId = pecaId().toString(), quantidade = quantidadePeca)),
            ),
        )

    private fun criarServico(): ServicoDto {
        val response =
            mockMvc
                .post("/servicos") {
                    contentType = MediaType.APPLICATION_JSON
                    content = servicoJson()
                }.andExpect { status { isCreated() } }
                .andReturn()

        return objectMapper.readValue(response.response.contentAsString)
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve criar servico com status inicial RECEBIDA e recupera-lo por id`() {
        val response =
            mockMvc
                .post("/servicos") {
                    contentType = MediaType.APPLICATION_JSON
                    content = servicoJson()
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.id") { exists() }
                    jsonPath("$.status") { value("RECEBIDA") }
                    jsonPath("$.descricao") { value("Revisão completa") }
                }.andReturn()

        val criado: ServicoDto = objectMapper.readValue(response.response.contentAsString)

        mockMvc
            .get("/servicos/${criado.id}")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(criado.id.toString()) }
                jsonPath("$.pecas.length()") { value(1) }
            }
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve calcular o orcamento somando preco de venda vezes quantidade`() {
        val criado = criarServico()

        // PEC001 tem preço de venda 45.00 e quantidade 2 → total 90.00
        mockMvc
            .get("/servicos/${criado.id}/orcamento")
            .andExpect {
                status { isOk() }
                jsonPath("$.servicoId") { value(criado.id.toString()) }
                jsonPath("$.itens.length()") { value(1) }
                jsonPath("$.itens[0].codigo") { value("PEC001") }
                jsonPath("$.valorTotal") { value(90.0) }
            }
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve avancar o status seguindo o fluxo da maquina de estados`() {
        val criado = criarServico()

        mockMvc
            .patch("/servicos/${criado.id}/avancar")
            .andExpect {
                status { isOk() }
                jsonPath("$.status") { value("EM_DIAGNOSTICO") }
            }

        mockMvc
            .patch("/servicos/${criado.id}/avancar")
            .andExpect {
                status { isOk() }
                jsonPath("$.status") { value("AGUARDANDO_APROVACAO") }
            }
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve retornar 422 em transicao de status nao permitida`() {
        val criado = criarServico()

        val dto = objectMapper.writeValueAsString(AlterarStatusDto(ServicoStatus.FINALIZADA))

        // De RECEBIDA só é permitido avançar para EM_DIAGNOSTICO.
        mockMvc
            .patch("/servicos/${criado.id}/status") {
                contentType = MediaType.APPLICATION_JSON
                content = dto
            }.andExpect { status { isUnprocessableEntity() } }
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve listar servicos por cliente`() {
        val criado = criarServico()

        mockMvc
            .get("/servicos/cliente/{clienteId}", clienteId())
            .andExpect {
                status { isOk() }
                jsonPath("$") { isArray() }
                jsonPath("$[?(@.id == '${criado.id}')]") { isNotEmpty() }
            }
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve retornar metricas de tempo medio de execucao`() {
        // No seed nenhum serviço possui dataInicioExecucao + dataFinalizacao,
        // portanto a média é nula e o total de finalizados é zero.
        mockMvc
            .get("/servicos/metricas/tempo-medio")
            .andExpect {
                status { isOk() }
                jsonPath("$.totalServicosFinalizados") { value(0) }
            }
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve deletar servico retornando 200`() {
        val criado = criarServico()

        mockMvc
            .delete("/servicos/${criado.id}")
            .andExpect { status { isOk() } }

        mockMvc
            .get("/servicos/${criado.id}")
            .andExpect {
                status { isOk() }
                content { string("") }
            }
    }

    @Test
    @WithMockUser(roles = ["CLIENTE"])
    fun `deve retornar 403 quando cliente tenta criar servico`() {
        mockMvc
            .post("/servicos") {
                contentType = MediaType.APPLICATION_JSON
                content = servicoJson()
            }.andExpect { status { isForbidden() } }
    }

    @Test
    fun `deve retornar 401 quando nao autenticado`() {
        mockMvc
            .get("/servicos")
            .andExpect { status { isUnauthorized() } }
    }
}
