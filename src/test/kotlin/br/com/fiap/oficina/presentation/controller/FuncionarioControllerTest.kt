package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.anyObject
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

    private val idCadastro = "00000000-0000-0000-0000-000000000050"
    private val idBusca = "00000000-0000-0000-0000-000000000010"
    private val idNaoEncontrado = "00000000-0000-0000-0000-000000000999"
    private val idAlteracao = "00000000-0000-0000-0000-000000000030"
    private val idRemocao = "00000000-0000-0000-0000-000000000040"
    private val idVini = "00000000-0000-0000-0000-000000000020"

    private fun funcionarioDto(id: String = idCadastro, nome: String = "João", cargo: String = "ATENDENTE") =
        FuncionarioDto(id = id, nome = nome, cargo = cargo)

    @Test
    @WithMockUser
    fun `deve cadastrar funcionario via endpoint POST`() {
        val requestJson = """{ "nome": "João", "cargo": "ATENDENTE" }"""

        `when`(service.cadastrar(anyObject())).thenReturn(funcionarioDto())

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
        `when`(service.listarTodos()).thenReturn(
            listOf(
                funcionarioDto(),
                funcionarioDto(id = "00000000-0000-0000-0000-000000000002", nome = "B", cargo = "MECANICO"),
            ),
        )

        mockMvc
            .perform(get("/funcionarios"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
    }

    @Test
    @WithMockUser
    fun `deve buscar por id via GET`() {
        `when`(service.buscarPorId(idBusca)).thenReturn(funcionarioDto(id = idBusca))

        mockMvc
            .perform(get("/funcionarios/id/$idBusca"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("João"))
    }

    @Test
    @WithMockUser
    fun `buscar por id quando nao encontrado deve retornar ok com corpo nulo`() {
        `when`(service.buscarPorId(idNaoEncontrado)).thenReturn(null)

        mockMvc
            .perform(get("/funcionarios/id/$idNaoEncontrado"))
            .andExpect(status().isOk)
            .andExpect(content().string(org.hamcrest.Matchers.any(String::class.java)))
    }

    @Test
    @WithMockUser
    fun `deve buscar por nome via GET nome path variable`() {
        `when`(service.buscarPorNome("Vini")).thenReturn(
            funcionarioDto(id = idVini, nome = "Vini", cargo = "MECANICO"),
        )

        mockMvc
            .perform(get("/funcionarios/nome/Vini"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Vini"))
    }

    @Test
    @WithMockUser
    fun `deve alterar funcionario via PUT`() {
        val requestJson = """{ "id": "$idAlteracao", "nome": "Alterado", "cargo": "MECANICO" }"""

        `when`(service.editar(anyObject(), anyObject())).thenReturn(
            funcionarioDto(id = idAlteracao, nome = "Alterado", cargo = "MECANICO"),
        )

        mockMvc
            .perform(
                put("/funcionarios/$idAlteracao")
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
                delete("/funcionarios/$idRemocao")
                    .with(SecurityMockMvcRequestPostProcessors.csrf()),
            ).andExpect(status().isOk)
    }
}
