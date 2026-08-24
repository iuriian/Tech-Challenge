package br.com.fiap.oficina.presentation.servico.controller

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.servico.usecase.AtualizarServicoUseCase
import br.com.fiap.oficina.application.servico.usecase.BuscarServicoUseCase
import br.com.fiap.oficina.application.servico.usecase.CriarServicoUseCase
import br.com.fiap.oficina.application.servico.usecase.DesativarServicoUseCase
import br.com.fiap.oficina.application.servico.usecase.ListarServicosAtivosUseCase
import br.com.fiap.oficina.application.servico.usecase.ListarTodosServicosUseCase
import br.com.fiap.oficina.application.servico.usecase.ReativarServicoUseCase
import br.com.fiap.oficina.domain.servico.Servico
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.servico.mapper.ServicoPresentationMapper
import br.com.fiap.oficina.presentation.servico.request.AtualizarServicoRequest
import br.com.fiap.oficina.presentation.servico.request.CriarServicoRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.math.BigDecimal
import java.util.UUID

class ServicoCatalogoControllerTest {
    private val criarServicoUseCase = mock(CriarServicoUseCase::class.java)
    private val buscarServicoUseCase = mock(BuscarServicoUseCase::class.java)
    private val atualizarServicoUseCase = mock(AtualizarServicoUseCase::class.java)
    private val listarServicosAtivosUseCase = mock(ListarServicosAtivosUseCase::class.java)
    private val listarTodosServicosUseCase = mock(ListarTodosServicosUseCase::class.java)
    private val desativarServicoUseCase = mock(DesativarServicoUseCase::class.java)
    private val reativarServicoUseCase = mock(ReativarServicoUseCase::class.java)

    private val controller =
        ServicoCatalogoController(
            criarServicoUseCase = criarServicoUseCase,
            buscarServicoUseCase = buscarServicoUseCase,
            atualizarServicoUseCase = atualizarServicoUseCase,
            listarServicosAtivosUseCase = listarServicosAtivosUseCase,
            listarTodosServicosUseCase = listarTodosServicosUseCase,
            desativarServicoUseCase = desativarServicoUseCase,
            reativarServicoUseCase = reativarServicoUseCase,
            mapper = ServicoPresentationMapper(),
        )

    private val id = UUID.randomUUID()

    private val servico =
        Servico(
            id = Id(id),
            descricao = "Alinhamento",
            valor = BigDecimal("120.00"),
            ativo = true,
        )

    @Test
    fun `criar deve retornar servico criado`() {
        `when`(
            criarServicoUseCase.executar(anyObject()),
        ).thenReturn(servico)

        val response =
            controller.criar(
                CriarServicoRequest(
                    descricao = "Alinhamento",
                    valor = BigDecimal("120.00"),
                ),
            )

        assertEquals(id, response.id)
        assertEquals("Alinhamento", response.descricao)
        assertEquals(BigDecimal("120.00"), response.valor)
        assertTrue(response.ativo)
    }

    @Test
    fun `listar ativos deve retornar servicos ativos`() {
        `when`(
            listarServicosAtivosUseCase.executar(),
        ).thenReturn(listOf(servico))

        val response = controller.listarAtivos()

        assertEquals(1, response.size)
        assertEquals(id, response.first().id)
        assertTrue(response.first().ativo)
    }

    @Test
    fun `listar todos deve retornar ativos e inativos`() {
        val inativo = servico.desativar()

        `when`(
            listarTodosServicosUseCase.executar(),
        ).thenReturn(listOf(servico, inativo))

        val response = controller.listarTodos()

        assertEquals(2, response.size)
        assertTrue(response[0].ativo)
        assertFalse(response[1].ativo)
    }

    @Test
    fun `buscar deve retornar servico pelo id`() {
        `when`(
            buscarServicoUseCase.executar(Id(id)),
        ).thenReturn(servico)

        val response = controller.buscar(id)

        assertEquals(id, response.id)
        assertEquals("Alinhamento", response.descricao)
    }

    @Test
    fun `atualizar deve retornar servico atualizado`() {
        val atualizado =
            servico
                .alterarDescricao("Alinhamento completo")
                .alterarValor(BigDecimal("150.00"))

        `when`(
            atualizarServicoUseCase.executar(
                anyObject(),
                anyObject(),
            ),
        ).thenReturn(atualizado)

        val response =
            controller.atualizar(
                id = id,
                request =
                    AtualizarServicoRequest(
                        descricao = "Alinhamento completo",
                        valor = BigDecimal("150.00"),
                    ),
            )

        assertEquals(id, response.id)
        assertEquals("Alinhamento completo", response.descricao)
        assertEquals(BigDecimal("150.00"), response.valor)
    }

    @Test
    fun `desativar deve delegar ao caso de uso`() {
        `when`(
            desativarServicoUseCase.executar(Id(id)),
        ).thenReturn(servico.desativar())

        controller.desativar(id)

        verify(desativarServicoUseCase).executar(Id(id))
    }

    @Test
    fun `reativar deve retornar servico reativado`() {
        val reativado = servico.desativar().reativar()

        `when`(
            reativarServicoUseCase.executar(Id(id)),
        ).thenReturn(reativado)

        val response = controller.reativar(id)

        assertEquals(id, response.id)
        assertTrue(response.ativo)
    }
}