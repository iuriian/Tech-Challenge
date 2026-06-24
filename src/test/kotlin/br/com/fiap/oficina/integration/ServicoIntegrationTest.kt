package br.com.fiap.oficina.integration

import br.com.fiap.oficina.domain.enum.ServicoStatus
import br.com.fiap.oficina.infrastructure.persistence.entity.ClienteJpaEntity
import br.com.fiap.oficina.infrastructure.persistence.entity.DocumentoEmbeddable
import br.com.fiap.oficina.infrastructure.persistence.entity.VeiculoJpaEntity
import br.com.fiap.oficina.infrastructure.persistence.repository.ClienteJpaRepository
import br.com.fiap.oficina.infrastructure.persistence.repository.VeiculoJpaRepository
import br.com.fiap.oficina.domain.valueobject.TipoPessoa
import br.com.fiap.oficina.presentation.dto.ServicoDto
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.greaterThan
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@Transactional
class ServicoIntegrationTest {

    @MockitoBean
    lateinit var jwtDecoder: JwtDecoder

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var clienteJpaRepository: ClienteJpaRepository

    @Autowired
    lateinit var veiculoJpaRepository: VeiculoJpaRepository

    private var clienteId: Long = 0L
    private var veiculoId: Long = 0L

    @BeforeEach
    fun setUp() {
        val cliente = ClienteJpaEntity().apply {
            nome = "Cliente Integração"
            email = "integracao@teste.com"
            documento = DocumentoEmbeddable().apply {
                numero = "39053344705"
                tipoPessoa = TipoPessoa.PESSOA_FISICA
            }
        }
        clienteId = clienteJpaRepository.save(cliente).id!!

        val veiculo = VeiculoJpaEntity().apply {
            marca = "Toyota"
            nome = "Corolla"
            modelo = "XEI"
            ano = "2022"
            placa = "ABC1234"
            motorista = cliente
        }
        veiculoId = veiculoJpaRepository.save(veiculo).idVeiculo!!
    }

    private fun novaOs(descricao: String = "Revisão completa") = ServicoDto(
        descricao = descricao,
        status = ServicoStatus.RECEBIDA,
        funcionarioId = 1L,
        clienteId = clienteId,
        veiculoId = veiculoId,
        pecasIds = emptyList()
    )

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve criar ordem de servico e consultar por id`() {
        val criarResult = mockMvc.perform(
            post("/servicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novaOs()))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.descricao").value("Revisão completa"))
            .andExpect(jsonPath("$.status").value("RECEBIDA"))
            .andReturn()

        val id = objectMapper.readTree(criarResult.response.contentAsString)["id"].asLong()

        mockMvc.perform(get("/servicos/$id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.descricao").value("Revisão completa"))
            .andExpect(jsonPath("$.clienteId").value(clienteId))
            .andExpect(jsonPath("$.veiculoId").value(veiculoId))
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve atualizar descricao de uma ordem de servico`() {
        val criarResult = mockMvc.perform(
            post("/servicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novaOs("Troca de óleo")))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val id = objectMapper.readTree(criarResult.response.contentAsString)["id"].asLong()

        mockMvc.perform(
            put("/servicos/$id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novaOs("Troca de óleo e filtro")))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.descricao").value("Troca de óleo e filtro"))
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve deletar ordem de servico`() {
        val criarResult = mockMvc.perform(
            post("/servicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novaOs("Alinhamento e balanceamento")))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val id = objectMapper.readTree(criarResult.response.contentAsString)["id"].asLong()

        mockMvc.perform(delete("/servicos/$id"))
            .andExpect(status().isNoContent)

        // após deletar, GET por id retorna 200 com corpo vazio (repositório devolve null)
        mockMvc.perform(get("/servicos/$id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").doesNotExist())
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve listar todas as ordens de servico`() {
        mockMvc.perform(
            post("/servicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novaOs("Substituição de correia dentada")))
        ).andExpect(status().isCreated)

        mockMvc.perform(get("/servicos"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(greaterThan(0)))
    }
}
