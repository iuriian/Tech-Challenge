package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.mapper.PecaApplicationMapper
import br.com.fiap.oficina.application.service.PecaService
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.exception.PecaNaoEncontradoException
import br.com.fiap.oficina.domain.usecase.peca.AtualizarPecaUseCase
import br.com.fiap.oficina.domain.usecase.peca.BuscarPecaPorCodigoUseCase
import br.com.fiap.oficina.domain.usecase.peca.BuscarPecaPorNomeUseCase
import br.com.fiap.oficina.domain.usecase.peca.CriarPecaUseCase
import br.com.fiap.oficina.domain.usecase.peca.DeletarPecaUseCase
import br.com.fiap.oficina.domain.usecase.peca.ListarPecasUseCase
import br.com.fiap.oficina.domain.usecase.peca.ReativarPecaUseCase
import br.com.fiap.oficina.domain.usecase.peca.ReporPecasUseCase
import br.com.fiap.oficina.domain.usecase.peca.RetirarPecasUseCase
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.exception.PecaExceptionHandler
import br.com.fiap.oficina.presentation.mapper.PecaMapper
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

@WebMvcTest(PecaController::class)
@Import(PecaService::class, PecaApplicationMapper::class, PecaMapper::class, PecaExceptionHandler::class)
class PecaControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var criarPecaUseCase: CriarPecaUseCase

    @MockitoBean
    lateinit var atualizarPecaUseCase: AtualizarPecaUseCase

    @MockitoBean
    lateinit var retirarPecasUseCase: RetirarPecasUseCase

    @MockitoBean
    lateinit var reporPecasUseCase: ReporPecasUseCase

    @MockitoBean
    lateinit var reativarPecaUseCase: ReativarPecaUseCase

    @MockitoBean
    lateinit var deletarPecaUseCase: DeletarPecaUseCase

    @MockitoBean
    lateinit var listarPecasUseCase: ListarPecasUseCase

    @MockitoBean
    lateinit var buscarPecaPorCodigoUseCase: BuscarPecaPorCodigoUseCase

    @MockitoBean
    lateinit var buscarPecaPorNomeUseCase: BuscarPecaPorNomeUseCase

    private val pecaId = "00000000-0000-0000-0000-000000000020"

    private val peca =
        Peca(
            id = Id.fromString(pecaId),
            codigo = "PEC001",
            nome = "Filtro de Óleo",
            precoDeVenda = BigDecimal("45.00"),
            qtdEstoque = 10,
        )

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `deve criar peca via endpoint POST`() {
        val requestJson =
            """
            {
              "codigo": "PEC001",
              "nome": "Filtro de Óleo",
              "precoDeVenda": 45.00,
              "qtdEstoque": 10
            }
            """.trimIndent()

        `when`(criarPecaUseCase.executar(anyObject())).thenReturn(peca)

        mockMvc
            .perform(
                post("/pecas")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson),
            ).andExpect(status().isCreated)
            .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/pecas/codigo/PEC001")))
            .andExpect(jsonPath("$.codigo").value("PEC001"))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `criar peca com codigo duplicado deve retornar 409`() {
        val requestJson =
            """
            {
              "codigo": "PEC001",
              "nome": "Filtro de Óleo",
              "precoDeVenda": 45.00,
              "qtdEstoque": 10
            }
            """.trimIndent()

        `when`(criarPecaUseCase.executar(anyObject())).thenThrow(IllegalArgumentException("Peça já cadastrada"))

        mockMvc
            .perform(
                post("/pecas")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson),
            ).andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("Peça já cadastrada"))
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve listar pecas via GET`() {
        `when`(listarPecasUseCase.executar()).thenReturn(listOf(peca))

        mockMvc
            .perform(get("/pecas"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve buscar peca por codigo via GET`() {
        `when`(buscarPecaPorCodigoUseCase.executar("PEC001")).thenReturn(peca)

        mockMvc
            .perform(get("/pecas/codigo/PEC001"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.codigo").value("PEC001"))
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `buscar por codigo inexistente deve retornar 404`() {
        `when`(buscarPecaPorCodigoUseCase.executar("XPTO"))
            .thenThrow(PecaNaoEncontradoException.porCodigo("XPTO"))

        mockMvc
            .perform(get("/pecas/codigo/XPTO"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Peça não encontrada com o código: XPTO"))
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve buscar peca por nome via GET`() {
        `when`(buscarPecaPorNomeUseCase.executar("Filtro de Óleo")).thenReturn(peca)

        mockMvc
            .perform(get("/pecas/nome/Filtro de Óleo"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Filtro de Óleo"))
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `buscar por nome inexistente deve retornar 404`() {
        `when`(buscarPecaPorNomeUseCase.executar("Inexistente"))
            .thenThrow(PecaNaoEncontradoException.porNome("Inexistente"))

        mockMvc
            .perform(get("/pecas/nome/Inexistente"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Peça não encontrada com o nome: Inexistente"))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `deve atualizar peca via PUT`() {
        val requestJson =
            """
            {
              "nome": "Filtro Novo",
              "precoDeVenda": 60.00
            }
            """.trimIndent()
        val atualizada = peca.copy(nome = "Filtro Novo", precoDeVenda = BigDecimal("60.00"))

        `when`(atualizarPecaUseCase.executar(anyObject(), anyObject())).thenReturn(atualizada)

        mockMvc
            .perform(
                put("/pecas/PEC001")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Filtro Novo"))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `atualizar peca inexistente deve retornar 404`() {
        val requestJson =
            """
            {
              "nome": "Filtro Novo",
              "precoDeVenda": 60.00
            }
            """.trimIndent()

        `when`(atualizarPecaUseCase.executar(anyObject(), anyObject()))
            .thenThrow(PecaNaoEncontradoException.porCodigo("XPTO"))

        mockMvc
            .perform(
                put("/pecas/XPTO")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson),
            ).andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `deve desativar peca via DELETE`() {
        `when`(deletarPecaUseCase.executar("PEC001")).thenReturn(true)

        mockMvc
            .perform(
                delete("/pecas/PEC001")
                    .with(SecurityMockMvcRequestPostProcessors.csrf()),
            ).andExpect(status().isNoContent)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `desativar peca inexistente deve retornar 404`() {
        `when`(deletarPecaUseCase.executar("XPTO"))
            .thenThrow(PecaNaoEncontradoException.porCodigo("XPTO"))

        mockMvc
            .perform(
                delete("/pecas/XPTO")
                    .with(SecurityMockMvcRequestPostProcessors.csrf()),
            ).andExpect(status().isNotFound)
    }
}
