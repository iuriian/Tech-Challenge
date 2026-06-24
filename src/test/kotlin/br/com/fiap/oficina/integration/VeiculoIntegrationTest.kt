package br.com.fiap.oficina.integration

import br.com.fiap.oficina.infrastructure.persistence.entity.ClienteJpaEntity
import br.com.fiap.oficina.infrastructure.persistence.entity.DocumentoEmbeddable
import br.com.fiap.oficina.infrastructure.persistence.repository.ClienteJpaRepository
import br.com.fiap.oficina.infrastructure.persistence.repository.VeiculoJpaRepository
import br.com.fiap.oficina.domain.valueobject.TipoPessoa
import com.fasterxml.jackson.databind.ObjectMapper
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@Transactional
class VeiculoIntegrationTest {

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

    @BeforeEach
    fun setUp() {
        val cliente = ClienteJpaEntity().apply {
            nome = "João da Silva"
            email = "joao@teste.com"
            documento = DocumentoEmbeddable().apply {
                numero = "39053344705"
                tipoPessoa = TipoPessoa.PESSOA_FISICA
            }
        }
        clienteId = clienteJpaRepository.save(cliente).id!!
    }

    // Motorista como mapa para evitar dependência do construtor do domain entity
    private fun novoVeiculo(placa: String = "ABC4567") = mapOf(
        "nome" to "Civic",
        "marca" to "Honda",
        "modelo" to "EXL",
        "ano" to "2023",
        "placa" to placa,
        "motorista" to mapOf(
            "id" to clienteId,
            "nome" to "João da Silva",
            "documento" to mapOf(
                "numero" to "39053344705",
                "tipoPessoa" to "PESSOA_FISICA"
            ),
            "email" to "joao@teste.com"
        )
    )

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve criar veiculo e buscar por placa`() {
        mockMvc.perform(
            post("/v1/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novoVeiculo()))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Civic"))
            .andExpect(jsonPath("$.placa").value("ABC4567"))

        mockMvc.perform(get("/v1/veiculos/placa/ABC4567"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Civic"))
            .andExpect(jsonPath("$.marca").value("Honda"))
            .andExpect(jsonPath("$.placa").value("ABC4567"))
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve criar veiculo e buscar por id`() {
        mockMvc.perform(
            post("/v1/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novoVeiculo("DEF7890")))
        ).andExpect(status().isOk)

        val id = veiculoJpaRepository.findByPlaca("DEF7890")!!.idVeiculo!!

        mockMvc.perform(get("/v1/veiculos/$id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.placa").value("DEF7890"))
            .andExpect(jsonPath("$.modelo").value("EXL"))
    }

    @Test
    @WithMockUser
    fun `deve retornar resposta vazia ao buscar veiculo inexistente por placa`() {
        mockMvc.perform(get("/v1/veiculos/placa/ZZZ9999"))
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser
    fun `deve retornar resposta vazia ao buscar veiculo inexistente por id`() {
        mockMvc.perform(get("/v1/veiculos/99999"))
            .andExpect(status().isOk)
    }
}
