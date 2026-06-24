package br.com.fiap.oficina.integration

import br.com.fiap.oficina.presentation.dto.PecaAtualizacaoDto
import br.com.fiap.oficina.presentation.dto.PecaDto
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@Transactional
class PecaIntegrationTest {

    @MockitoBean
    lateinit var jwtDecoder: JwtDecoder

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private fun novaPeca(codigo: String = "FLT001", nome: String = "Filtro de óleo") = PecaDto(
        codigo = codigo,
        nome = nome,
        descricao = "Filtro de alta qualidade",
        precoDeVenda = BigDecimal("29.90"),
        qtdEstoque = 10
    )

    private fun criarPeca(peca: PecaDto = novaPeca()): PecaDto {
        val result = mockMvc.perform(
            post("/pecas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(peca))
        ).andExpect(status().isOk).andReturn()

        return objectMapper.readValue(result.response.contentAsString, PecaDto::class.java)
    }

    @Test
    @WithMockUser
    fun `deve criar peca e buscar por codigo`() {
        criarPeca()

        mockMvc.perform(get("/pecas/codigo/FLT001"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.codigo").value("FLT001"))
            .andExpect(jsonPath("$.nome").value("Filtro de óleo"))
            .andExpect(jsonPath("$.qtdEstoque").value(10))
            .andExpect(jsonPath("$.ativo").value(true))
    }

    @Test
    @WithMockUser
    fun `deve criar peca e listar todas as pecas ativas`() {
        criarPeca(novaPeca("FLT002", "Filtro de ar"))

        mockMvc.perform(get("/pecas"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].codigo").exists())
    }

    @Test
    @WithMockUser
    fun `deve buscar peca por nome`() {
        criarPeca(novaPeca("VLV001", "Válvula de escape"))

        mockMvc.perform(get("/pecas/nome/Válvula de escape"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.codigo").value("VLV001"))
    }

    @Test
    @WithMockUser
    fun `deve atualizar dados da peca`() {
        criarPeca()

        val atualizacao = PecaAtualizacaoDto(
            nome = "Filtro de óleo sintético",
            descricao = "Alta performance",
            precoDeVenda = BigDecimal("39.90")
        )
        mockMvc.perform(
            put("/pecas/FLT001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atualizacao))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Filtro de óleo sintético"))
            .andExpect(jsonPath("$.precoDeVenda").value(39.90))
    }

    @Test
    @WithMockUser
    fun `deve retirar e repor unidades no estoque`() {
        criarPeca() // qtdEstoque = 10

        mockMvc.perform(patch("/pecas/FLT001/estoque/retirar").param("qtd", "3"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.qtdEstoque").value(7))

        mockMvc.perform(patch("/pecas/FLT001/estoque/repor").param("qtd", "5"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.qtdEstoque").value(12))
    }

    @Test
    @WithMockUser
    fun `deve desativar peca via delete e depois reativar`() {
        criarPeca()

        mockMvc.perform(delete("/pecas/FLT001"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").value(true))

        // após desativar, busca ativa não encontra mais — IllegalArgumentException propaga pelo MockMvc
        assertThrows<Exception> {
            mockMvc.perform(get("/pecas/codigo/FLT001")).andReturn()
        }

        // reativa
        mockMvc.perform(patch("/pecas/FLT001/reativar"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").value(true))

        // agora encontra novamente
        mockMvc.perform(get("/pecas/codigo/FLT001"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.ativo").value(true))
    }

    @Test
    @WithMockUser
    fun `deve rejeitar criacao de peca com codigo duplicado`() {
        criarPeca()

        mockMvc.perform(
            post("/pecas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novaPeca()))
        ).andExpect(status().isConflict)
    }
}
