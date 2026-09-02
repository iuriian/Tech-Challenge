package br.com.fiap.oficina.integration

import br.com.fiap.oficina.application.dto.VeiculoRequest
import br.com.fiap.oficina.application.dto.VeiculoResponse
import br.com.fiap.oficina.domain.repository.ClienteRepository
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.util.UUID

/**
 * Testes de integração do fluxo de veículos. Reutiliza o motorista de seed
 * (João da Silva, CPF 39053344705) e suas placas (ABC1D23 / DEF2G34).
 */
class VeiculoControllerIntegrationTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var clienteRepository: ClienteRepository

    private fun motoristaSeedId(): UUID = clienteRepository.buscarPorDocumento("39053344705")!!.id.valor

    private fun novoVeiculoJson(
        placa: String = "TST1A23",
        motoristaId: String = motoristaSeedId().toString(),
        nome: String = "Carro de Teste",
    ) = objectMapper.writeValueAsString(
        VeiculoRequest(
            nome = nome,
            marca = "Honda",
            modelo = "Civic",
            ano = "2023",
            placa = placa,
            motoristaId = motoristaId,
        ),
    )

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve criar veiculo e recupera-lo por id`() {
        val response =
            mockMvc
                .post("/veiculos") {
                    contentType = MediaType.APPLICATION_JSON
                    content = novoVeiculoJson()
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.id") { exists() }
                    jsonPath("$.placa") { value("TST1A23") }
                }.andReturn()

        val criado: VeiculoResponse = objectMapper.readValue(response.response.contentAsString)

        mockMvc
            .get("/veiculos/${criado.id}")
            .andExpect {
                status { isOk() }
                jsonPath("$.placa") { value("TST1A23") }
                jsonPath("$.marca") { value("Honda") }
            }
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve retornar 409 ao cadastrar placa ja existente`() {
        mockMvc
            .post("/veiculos") {
                contentType = MediaType.APPLICATION_JSON
                content = novoVeiculoJson(placa = "ABC1D23") // placa do seed (Gol do João)
            }.andExpect { status { isConflict() } }
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve retornar 400 quando placa tem formato invalido`() {
        mockMvc
            .post("/veiculos") {
                contentType = MediaType.APPLICATION_JSON
                content = novoVeiculoJson(placa = "INVALIDA")
            }.andExpect { status { isBadRequest() } }
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve buscar veiculo de seed por placa`() {
        mockMvc
            .get("/veiculos/placa/{placa}", "ABC1D23")
            .andExpect {
                status { isOk() }
                jsonPath("$.modelo") { value("Gol 1.6") }
            }
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve listar veiculos do motorista de seed`() {
        mockMvc
            .get("/veiculos/motorista/{id}", motoristaSeedId().toString())
            .andExpect {
                status { isOk() }
                jsonPath("$") { isArray() }
                // João possui dois veículos no seed (ABC1D23 e DEF2G34).
                jsonPath("$[?(@.placa == 'ABC1D23')]") { isNotEmpty() }
                jsonPath("$[?(@.placa == 'DEF2G34')]") { isNotEmpty() }
            }
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve atualizar um veiculo existente`() {
        val response =
            mockMvc
                .post("/veiculos") {
                    contentType = MediaType.APPLICATION_JSON
                    content = novoVeiculoJson(placa = "TST2B34")
                }.andExpect { status { isCreated() } }
                .andReturn()

        val criado: VeiculoResponse = objectMapper.readValue(response.response.contentAsString)

        val atualizado =
            objectMapper.writeValueAsString(
                VeiculoRequest(
                    nome = criado.nome,
                    marca = criado.marca,
                    modelo = "Civic Touring",
                    ano = criado.ano,
                    placa = criado.placa,
                    motoristaId = criado.motoristaId,
                ),
            )

        mockMvc
            .put("/veiculos/${criado.id}") {
                contentType = MediaType.APPLICATION_JSON
                content = atualizado
            }.andExpect {
                status { isOk() }
                jsonPath("$.modelo") { value("Civic Touring") }
            }
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `deve remover veiculo retornando 200`() {
        val response =
            mockMvc
                .post("/veiculos") {
                    contentType = MediaType.APPLICATION_JSON
                    content = novoVeiculoJson(placa = "TST3C45")
                }.andExpect { status { isCreated() } }
                .andReturn()

        val criado: VeiculoResponse = objectMapper.readValue(response.response.contentAsString)

        mockMvc
            .delete("/veiculos/${criado.id}")
            .andExpect { status { isOk() } }
    }

    @Test
    @WithMockUser(roles = ["MECANICO"])
    fun `deve retornar 403 quando papel nao tem permissao para criar`() {
        mockMvc
            .post("/veiculos") {
                contentType = MediaType.APPLICATION_JSON
                content = novoVeiculoJson()
            }.andExpect { status { isForbidden() } }
    }
}
