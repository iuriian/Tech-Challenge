package br.com.fiap.oficina.integration

import br.com.fiap.oficina.infrastructure.persistence.repository.ClienteJpaRepository
import br.com.fiap.oficina.presentation.dto.ClienteDto
import com.fasterxml.jackson.databind.ObjectMapper
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
class ClienteIntegrationTest {

    @MockitoBean
    lateinit var jwtDecoder: JwtDecoder

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var clienteJpaRepository: ClienteJpaRepository

    private fun novoCliente(
        nome: String = "Maria da Silva",
        doc: String = "39053344705",
        email: String = "maria@teste.com"
    ) = ClienteDto(
        nome = nome,
        numeroDocumento = doc,
        tipoPessoa = "PESSOA_FISICA",
        email = email
    )

    private fun criarCliente(dto: ClienteDto = novoCliente()): Long {
        mockMvc.perform(
            post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk)

        return clienteJpaRepository.findByDocumentoNumero(dto.numeroDocumento)!!.id!!
    }

    @Test
    @WithMockUser
    fun `deve criar cliente e buscar por id`() {
        val id = criarCliente()

        mockMvc.perform(get("/clientes/$id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Maria da Silva"))
            .andExpect(jsonPath("$.numeroDocumento").value("39053344705"))
            .andExpect(jsonPath("$.tipoPessoa").value("PESSOA_FISICA"))
            .andExpect(jsonPath("$.email").value("maria@teste.com"))
    }

    @Test
    @WithMockUser
    fun `deve buscar cliente por nome`() {
        criarCliente()

        mockMvc.perform(get("/clientes/nome/Maria da Silva"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Maria da Silva"))
            .andExpect(jsonPath("$.email").value("maria@teste.com"))
    }

    @Test
    @WithMockUser
    fun `deve buscar cliente por documento`() {
        criarCliente()

        mockMvc.perform(get("/clientes/documento/39053344705"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Maria da Silva"))
            .andExpect(jsonPath("$.numeroDocumento").value("39053344705"))
    }

    @Test
    @WithMockUser
    fun `deve atualizar dados do cliente`() {
        val id = criarCliente()

        val atualizado = novoCliente(nome = "Maria Santos", email = "maria.santos@teste.com")
        mockMvc.perform(
            put("/clientes/$id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atualizado))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Maria Santos"))
            .andExpect(jsonPath("$.email").value("maria.santos@teste.com"))
    }

    @Test
    @WithMockUser
    fun `deve remover cliente`() {
        val id = criarCliente()

        mockMvc.perform(delete("/clientes/$id"))
            .andExpect(status().isOk)

        mockMvc.perform(get("/clientes/$id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").doesNotExist())
    }
}
