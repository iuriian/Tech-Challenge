package br.com.fiap.oficina.integration

import br.com.fiap.oficina.presentation.dto.ClienteDto
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

/**
 * Testes de integração do fluxo de clientes, do HTTP ao PostgreSQL real.
 * CPF de seed usado nas consultas: 39053344705 (João da Silva).
 */
class ClienteControllerIntegrationTest : AbstractIntegrationTest() {
    private val cpfSeedJoao = "39053344705"
    private val cpfValidoNovo = "52998224725"

    private fun novoClienteJson(
        nome: String = "Cliente de Teste",
        documento: String = cpfValidoNovo,
        email: String = "cliente.teste@example.com",
    ) = objectMapper.writeValueAsString(
        ClienteDto(
            nome = nome,
            numeroDocumento = documento,
            tipoPessoa = "PESSOA_FISICA",
            email = email,
        ),
    )

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve criar cliente e recupera-lo por id persistindo no banco`() {
        val response =
            mockMvc
                .post("/clientes") {
                    contentType = MediaType.APPLICATION_JSON
                    content = novoClienteJson()
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.id") { exists() }
                    jsonPath("$.nome") { value("Cliente de Teste") }
                    jsonPath("$.numeroDocumento") { value(cpfValidoNovo) }
                }.andReturn()

        val criado: ClienteDto = objectMapper.readValue(response.response.contentAsString)

        mockMvc
            .get("/clientes/${criado.id}")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(criado.id.toString()) }
                jsonPath("$.nome") { value("Cliente de Teste") }
            }
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve buscar cliente de seed por documento`() {
        mockMvc
            .get("/clientes/documento/$cpfSeedJoao")
            .andExpect {
                status { isOk() }
                jsonPath("$.nome") { value("João da Silva") }
                jsonPath("$.email") { value("joao.silva@example.com") }
            }
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve buscar cliente de seed por nome`() {
        mockMvc
            .get("/clientes/nome/{nome}", "João da Silva")
            .andExpect {
                status { isOk() }
                jsonPath("$.numeroDocumento") { value(cpfSeedJoao) }
            }
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `deve listar todos os clientes incluindo o seed`() {
        mockMvc
            .get("/clientes")
            .andExpect {
                status { isOk() }
                jsonPath("$") { isArray() }
                jsonPath("$[?(@.numeroDocumento == '$cpfSeedJoao')]") { isNotEmpty() }
            }
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve alterar dados de um cliente existente`() {
        val response =
            mockMvc
                .post("/clientes") {
                    contentType = MediaType.APPLICATION_JSON
                    content = novoClienteJson(email = "alterar@example.com")
                }.andExpect { status { isCreated() } }
                .andReturn()

        val criado: ClienteDto = objectMapper.readValue(response.response.contentAsString)

        val alterado =
            objectMapper.writeValueAsString(
                criado.copy(nome = "Nome Atualizado"),
            )

        mockMvc
            .put("/clientes/${criado.id}") {
                contentType = MediaType.APPLICATION_JSON
                content = alterado
            }.andExpect {
                status { isOk() }
                jsonPath("$.nome") { value("Nome Atualizado") }
            }
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `deve remover cliente retornando 204`() {
        val response =
            mockMvc
                .post("/clientes") {
                    contentType = MediaType.APPLICATION_JSON
                    content = novoClienteJson(documento = "16899535009", email = "remover@example.com")
                }.andExpect { status { isCreated() } }
                .andReturn()

        val criado: ClienteDto = objectMapper.readValue(response.response.contentAsString)

        mockMvc
            .delete("/clientes/${criado.id}")
            .andExpect { status { isNoContent() } }

        mockMvc
            .get("/clientes/${criado.id}")
            .andExpect {
                status { isOk() }
                content { string("") }
            }
    }

    @Test
    fun `deve retornar 401 quando nao autenticado`() {
        mockMvc
            .get("/clientes/documento/$cpfSeedJoao")
            .andExpect { status { isUnauthorized() } }
    }

    @Test
    @WithMockUser(roles = ["MECANICO"])
    fun `deve retornar 403 quando papel nao tem permissao`() {
        mockMvc
            .post("/clientes") {
                contentType = MediaType.APPLICATION_JSON
                content = novoClienteJson()
            }.andExpect { status { isForbidden() } }
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve retornar 400 quando payload viola bean validation`() {
        val invalido =
            objectMapper.writeValueAsString(
                ClienteDto(
                    nome = "ab", // viola @Size(min = 5)
                    numeroDocumento = cpfValidoNovo,
                    tipoPessoa = "PESSOA_FISICA",
                    email = "email-invalido", // viola @Email
                ),
            )

        mockMvc
            .post("/clientes") {
                contentType = MediaType.APPLICATION_JSON
                content = invalido
            }.andExpect { status { isBadRequest() } }
    }
}
