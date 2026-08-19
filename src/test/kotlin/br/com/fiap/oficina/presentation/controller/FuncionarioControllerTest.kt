package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.service.FuncionarioService
import br.com.fiap.oficina.presentation.dto.FuncionarioDto
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(FuncionarioController::class)
class FuncionarioControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var service: FuncionarioService

    @Test
    @WithMockUser
    fun `deve cadastrar funcionario via endpoint POST`() {
        val requestJson = """{ "nome": "João", "cargo": "ATENDENTE" }"""
        val responseDto = FuncionarioDto(id = "00000000-0000-0000-0000-000000000050", nome = "João", cargo = "ATENDENTE")

        `when`(service.cadastrar(FuncionarioDto(nome = "João", cargo = "ATENDENTE"))).thenReturn(responseDto)

        mockMvc
            .perform(
                post("/funcionarios")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson),
            ).andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.nome").value("João"))
            .andExpect(jsonPath("$.cargo").value("ATENDENTE"))
    }

    @Test
    @WithMockUser
    fun `deve listar todos os funcionarios via GET`() {
        val dto1 = FuncionarioDto(id = "1", nome = "A", cargo = "ATENDENTE")
        val dto2 = FuncionarioDto(id = "2", nome = "B", cargo = "MECANICO")

        `when`(service.listarTodos()).thenReturn(listOf(dto1, dto2))

        mockMvc
            .perform(get("/funcionarios"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
    }

    @Test
    @WithMockUser
    fun `deve buscar por id via GET`() {
        val dto = FuncionarioDto(id = "10", nome = "Busca", cargo = "ATENDENTE")
        `when`(service.buscarPorId("10")).thenReturn(dto)

        mockMvc
            .perform(get("/funcionarios/id/10"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Busca"))
    }

    @Test
    @WithMockUser
    fun `buscar por id quando nao encontrado deve retornar ok com corpo nulo`() {
        `when`(service.buscarPorId("999")).thenReturn(null)

        mockMvc
            .perform(get("/funcionarios/id/999"))
            .andExpect(status().isOk)
            .andExpect(content().string(org.hamcrest.Matchers.any(String::class.java)))
    }

    @Test
    @WithMockUser
    fun `deve buscar por nome via GET nome path variable`() {
        val dto = FuncionarioDto(id = "20", nome = "Vini", cargo = "MECANICO")
        `when`(service.buscarPorNome("Vini")).thenReturn(dto)

        mockMvc
            .perform(get("/funcionarios/nome/Vini"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Vini"))
    }

    @Test
    @WithMockUser
    fun `deve alterar funcionario via PUT`() {
        val requestJson = """{ "id": "30", "nome": "Alterado", "cargo": "MECANICO" }"""
        val responseDto = FuncionarioDto(id = "30", nome = "Alterado", cargo = "MECANICO")

        `when`(service.editar("30", FuncionarioDto(id = "30", nome = "Alterado", cargo = "MECANICO"))).thenReturn(responseDto)

        mockMvc
            .perform(
                put("/funcionarios/30")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Alterado"))
    }

    @Test
    @WithMockUser
    fun `deve deletar funcionario via DELETE`() {
        mockMvc
            .perform(
                delete(
                    "/funcionarios/40",
                ).with(SecurityMockMvcRequestPostProcessors.csrf()),
            ).andExpect(status().isOk)
    }
}
