package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.mapper.FuncionarioApplicationMapper
import br.com.fiap.oficina.application.service.FuncionarioService
import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.enum.Cargo
import br.com.fiap.oficina.domain.exception.FuncionarioNaoEncontradoException
import br.com.fiap.oficina.domain.usecase.funcionario.AtualizarFuncionarioUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.BuscarFuncionarioPorIdUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.BuscarFuncionarioPorNomeUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.CriarFuncionarioUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.ListarFuncionariosUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.RemoverFuncionarioUseCase
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.exception.FuncionarioExceptionHandler
import br.com.fiap.oficina.presentation.mapper.FuncionarioMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(FuncionarioController::class)
@Import(
    FuncionarioService::class,
    FuncionarioApplicationMapper::class,
    FuncionarioMapper::class,
    FuncionarioExceptionHandler::class,
)
class FuncionarioControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var criarFuncionarioUseCase: CriarFuncionarioUseCase

    @MockitoBean
    lateinit var listarFuncionariosUseCase: ListarFuncionariosUseCase

    @MockitoBean
    lateinit var buscarFuncionarioPorIdUseCase: BuscarFuncionarioPorIdUseCase

    @MockitoBean
    lateinit var buscarFuncionarioPorNomeUseCase: BuscarFuncionarioPorNomeUseCase

    @MockitoBean
    lateinit var atualizarFuncionarioUseCase: AtualizarFuncionarioUseCase

    @MockitoBean
    lateinit var removerFuncionarioUseCase: RemoverFuncionarioUseCase

    private val idCadastro = "00000000-0000-0000-0000-000000000050"
    private val idBusca = "00000000-0000-0000-0000-000000000010"
    private val idNaoEncontrado = "00000000-0000-0000-0000-000000000999"
    private val idAlteracao = "00000000-0000-0000-0000-000000000030"
    private val idRemocao = "00000000-0000-0000-0000-000000000040"
    private val idVini = "00000000-0000-0000-0000-000000000020"

    private val funcionario =
        Funcionario(
            id = Id.fromString(idCadastro),
            nome = "João",
            cargo = Cargo.ATENDENTE,
        )

    @Test
    @WithMockUser
    fun `deve cadastrar funcionario via endpoint POST`() {
        val requestJson = """{ "nome": "João", "cargo": "ATENDENTE" }"""

        `when`(criarFuncionarioUseCase.executar(anyObject())).thenReturn(funcionario)

        mockMvc
            .perform(
                post("/funcionarios")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson),
            ).andExpect(status().isCreated)
            .andExpect(
                header().string("Location", org.hamcrest.Matchers.containsString("/funcionarios/id/$idCadastro")),
            )
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.nome").value("João"))
            .andExpect(jsonPath("$.cargo").value("ATENDENTE"))
    }

    @Test
    @WithMockUser
    fun `deve listar todos os funcionarios via GET`() {
        val funcionario2 =
            Funcionario(
                id = Id.fromString("00000000-0000-0000-0000-000000000002"),
                nome = "B",
                cargo = Cargo.MECANICO,
            )

        `when`(listarFuncionariosUseCase.executar()).thenReturn(listOf(funcionario, funcionario2))

        mockMvc
            .perform(get("/funcionarios"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
    }

    @Test
    @WithMockUser
    fun `deve buscar por id via GET`() {
        `when`(buscarFuncionarioPorIdUseCase.executar(Id.fromString(idBusca))).thenReturn(funcionario)

        mockMvc
            .perform(get("/funcionarios/id/$idBusca"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("João"))
    }

    @Test
    @WithMockUser
    fun `buscar por id quando nao encontrado deve retornar 404`() {
        `when`(buscarFuncionarioPorIdUseCase.executar(Id.fromString(idNaoEncontrado)))
            .thenThrow(FuncionarioNaoEncontradoException.porId(idNaoEncontrado))

        mockMvc
            .perform(get("/funcionarios/id/$idNaoEncontrado"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Funcionário não encontrado com o ID: $idNaoEncontrado"))
    }

    @Test
    @WithMockUser
    fun `deve buscar por nome via GET nome path variable`() {
        val funcionarioVini =
            Funcionario(
                id = Id.fromString(idVini),
                nome = "Vini",
                cargo = Cargo.MECANICO,
            )

        `when`(buscarFuncionarioPorNomeUseCase.executar("Vini")).thenReturn(funcionarioVini)

        mockMvc
            .perform(get("/funcionarios/nome/Vini"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Vini"))
    }

    @Test
    @WithMockUser
    fun `buscar por nome quando nao encontrado deve retornar 404`() {
        `when`(buscarFuncionarioPorNomeUseCase.executar("Inexistente"))
            .thenThrow(FuncionarioNaoEncontradoException.porNome("Inexistente"))

        mockMvc
            .perform(get("/funcionarios/nome/Inexistente"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Funcionário não encontrado com o nome: Inexistente"))
    }

    @Test
    @WithMockUser
    fun `deve alterar funcionario via PUT`() {
        val requestJson = """{ "id": "$idAlteracao", "nome": "Alterado", "cargo": "MECANICO" }"""
        val atualizado =
            Funcionario(
                id = Id.fromString(idAlteracao),
                nome = "Alterado",
                cargo = Cargo.MECANICO,
            )

        `when`(atualizarFuncionarioUseCase.executar(anyObject())).thenReturn(atualizado)

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
    fun `alterar funcionario inexistente deve retornar 404`() {
        val requestJson = """{ "id": "$idNaoEncontrado", "nome": "Alterado", "cargo": "MECANICO" }"""

        `when`(atualizarFuncionarioUseCase.executar(anyObject()))
            .thenThrow(FuncionarioNaoEncontradoException.porId(idNaoEncontrado))

        mockMvc
            .perform(
                put("/funcionarios/$idNaoEncontrado")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson),
            ).andExpect(status().isNotFound)
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

    @Test
    @WithMockUser
    fun `deletar funcionario inexistente deve retornar 404`() {
        `when`(removerFuncionarioUseCase.executar(Id.fromString(idNaoEncontrado)))
            .thenThrow(FuncionarioNaoEncontradoException.porId(idNaoEncontrado))

        mockMvc
            .perform(
                delete("/funcionarios/$idNaoEncontrado")
                    .with(SecurityMockMvcRequestPostProcessors.csrf()),
            ).andExpect(status().isNotFound)
    }
}
