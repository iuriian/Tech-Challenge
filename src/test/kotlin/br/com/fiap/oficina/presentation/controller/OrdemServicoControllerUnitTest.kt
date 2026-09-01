package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.dto.AlterarStatusRequest
import br.com.fiap.oficina.application.dto.ItemOrcamentoResponse
import br.com.fiap.oficina.application.dto.OrcamentoResponse
import br.com.fiap.oficina.application.dto.OrdemServicoRequest
import br.com.fiap.oficina.application.dto.OrdemServicoResponse
import br.com.fiap.oficina.application.service.OrdemServicoService
import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.domain.enum.TipoItemOrcamento
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrdemServicoControllerUnitTest {
    private val service =
        mock(
            OrdemServicoService::class.java,
        )

    private val controller =
        OrdemServicoController(
            service = service,
        )

    @Test
    fun `criar deve retornar response do service`() {
        val request = request()
        val response = response()

        `when`(
            service.criar(request),
        ).thenReturn(response)

        val resultado =
            controller.criar(request)

        assertEquals(
            response.id,
            resultado.id,
        )

        assertEquals(
            response.descricao,
            resultado.descricao,
        )
    }

    @Test
    fun `atualizar deve retornar response do service`() {
        val id = UUID.randomUUID().toString()
        val request = request()
        val response = response()

        `when`(
            service.atualizar(
                id,
                request,
            ),
        ).thenReturn(response)

        val resultado =
            controller.atualizar(
                id,
                request,
            )

        assertEquals(
            response.id,
            resultado.id,
        )
    }

    @Test
    fun `listarTodos deve delegar ao service`() {
        `when`(
            service.listarTodos(),
        ).thenReturn(
            listOf(
                response(),
            ),
        )

        assertEquals(
            1,
            controller.listarTodos().size,
        )
    }

    @Test
    fun `listarPorCliente deve retornar lista`() {
        val clienteId =
            UUID.randomUUID().toString()

        val response =
            response(
                clienteId = clienteId,
            )

        `when`(
            service.listarPorCliente(clienteId),
        ).thenReturn(
            listOf(response),
        )

        val resultado =
            controller.listarPorCliente(
                clienteId,
            )

        assertEquals(1, resultado.size)
        assertEquals(
            clienteId,
            resultado.first().clienteId,
        )
    }

    @Test
    fun `listarPorCliente deve retornar lista vazia`() {
        val clienteId =
            UUID.randomUUID().toString()

        `when`(
            service.listarPorCliente(clienteId),
        ).thenReturn(emptyList())

        assertTrue(
            controller
                .listarPorCliente(
                    clienteId,
                ).isEmpty(),
        )
    }

    @Test
    fun `obterOrcamento deve retornar response`() {
        val id =
            UUID.randomUUID().toString()

        val orcamento =
            orcamentoResponse()

        `when`(
            service.obterOrcamento(id),
        ).thenReturn(orcamento)

        val resultado =
            controller.obterOrcamento(id)

        assertEquals(
            BigDecimal("20"),
            resultado.valorTotal,
        )

        assertEquals(
            1,
            resultado.itens.size,
        )
    }

    @Test
    fun `obterOrcamento deve retornar 404 quando ordem nao existe`() {
        val id =
            UUID.randomUUID().toString()

        `when`(
            service.obterOrcamento(id),
        ).thenThrow(
            IllegalArgumentException(
                "Ordem de serviço não encontrada com o ID: $id",
            ),
        )

        val exception =
            assertThrows(
                ResponseStatusException::class.java,
            ) {
                controller.obterOrcamento(id)
            }

        assertEquals(
            HttpStatus.NOT_FOUND,
            exception.statusCode,
        )
    }

    @Test
    fun `deletarPorId deve delegar ao service`() {
        val id =
            UUID.randomUUID().toString()

        controller.deletarPorId(id)

        verify(service)
            .deletarPorId(id)
    }

    @Test
    fun `avancarStatus deve retornar novo status`() {
        val id =
            UUID.randomUUID().toString()

        `when`(
            service.avancarStatus(id),
        ).thenReturn(
            response(
                status =
                OrdemServicoStatus.EM_DIAGNOSTICO,
            ),
        )

        val resultado =
            controller.avancarStatus(id)

        assertEquals(
            OrdemServicoStatus.EM_DIAGNOSTICO,
            resultado.status,
        )
    }

    @Test
    fun `avancarStatus deve retornar 404 quando ordem nao existe`() {
        val id =
            UUID.randomUUID().toString()

        `when`(
            service.avancarStatus(id),
        ).thenThrow(
            IllegalArgumentException(
                "Ordem de serviço não encontrada com o ID: $id",
            ),
        )

        val exception =
            assertThrows(
                ResponseStatusException::class.java,
            ) {
                controller.avancarStatus(id)
            }

        assertEquals(
            HttpStatus.NOT_FOUND,
            exception.statusCode,
        )
    }

    @Test
    fun `avancarStatus deve retornar 422 em estado final`() {
        val id =
            UUID.randomUUID().toString()

        `when`(
            service.avancarStatus(id),
        ).thenThrow(
            IllegalStateException(
                "Ordem de serviço está em estado final.",
            ),
        )

        val exception =
            assertThrows(
                ResponseStatusException::class.java,
            ) {
                controller.avancarStatus(id)
            }

        assertEquals(
            HttpStatus.UNPROCESSABLE_ENTITY,
            exception.statusCode,
        )
    }

    @Test
    fun `alterarStatus deve retornar status alterado`() {
        val id =
            UUID.randomUUID().toString()

        `when`(
            service.alterarStatus(
                id,
                OrdemServicoStatus.CANCELADA,
            ),
        ).thenReturn(
            response(
                status =
                OrdemServicoStatus.CANCELADA,
            ),
        )

        val resultado =
            controller.alterarStatus(
                id,
                AlterarStatusRequest(
                    OrdemServicoStatus.CANCELADA,
                ),
            )

        assertEquals(
            OrdemServicoStatus.CANCELADA,
            resultado.status,
        )
    }

    @Test
    fun `alterarStatus deve retornar 404 quando ordem nao existe`() {
        val id =
            UUID.randomUUID().toString()

        `when`(
            service.alterarStatus(
                id,
                OrdemServicoStatus.CANCELADA,
            ),
        ).thenThrow(
            IllegalArgumentException(
                "Ordem de serviço não encontrada com o ID: $id",
            ),
        )

        val exception =
            assertThrows(
                ResponseStatusException::class.java,
            ) {
                controller.alterarStatus(
                    id,
                    AlterarStatusRequest(
                        OrdemServicoStatus.CANCELADA,
                    ),
                )
            }

        assertEquals(
            HttpStatus.NOT_FOUND,
            exception.statusCode,
        )
    }

    @Test
    fun `alterarStatus deve retornar 422 para transicao invalida`() {
        val id =
            UUID.randomUUID().toString()

        `when`(
            service.alterarStatus(
                id,
                OrdemServicoStatus.ENTREGUE,
            ),
        ).thenThrow(
            IllegalStateException(
                "Transição inválida.",
            ),
        )

        val exception =
            assertThrows(
                ResponseStatusException::class.java,
            ) {
                controller.alterarStatus(
                    id,
                    AlterarStatusRequest(
                        OrdemServicoStatus.ENTREGUE,
                    ),
                )
            }

        assertEquals(
            HttpStatus.UNPROCESSABLE_ENTITY,
            exception.statusCode,
        )
    }

    private fun request(): OrdemServicoRequest = OrdemServicoRequest(
        descricao = "Troca de óleo",
        funcionarioId = UUID.randomUUID().toString(),
        clienteId = UUID.randomUUID().toString(),
        veiculoId = UUID.randomUUID().toString(),
    )

    private fun response(
        status: OrdemServicoStatus =
            OrdemServicoStatus.RECEBIDA,
        clienteId: String =
            UUID.randomUUID().toString(),
    ): OrdemServicoResponse = OrdemServicoResponse(
        id = UUID.randomUUID(),
        descricao = "Troca de óleo",
        status = status,
        funcionarioId =
        UUID.randomUUID().toString(),
        clienteId = clienteId,
        veiculoId =
        UUID.randomUUID().toString(),
        itens = emptyList(),
        dataAbertura = Instant.now(),
        dataInicioExecucao = null,
        dataFinalizacao = null,
    )

    private fun orcamentoResponse(): OrcamentoResponse = OrcamentoResponse(
        itens =
        listOf(
            ItemOrcamentoResponse(
                tipo =
                TipoItemOrcamento.PECA,
                referenciaId =
                UUID.randomUUID(),
                codigoReferencia =
                "PEC001",
                descricao =
                "Filtro",
                valorUnitario =
                BigDecimal.TEN,
                quantidade =
                BigDecimal("2"),
                subtotal =
                BigDecimal("20"),
            ),
        ),
        valorTotal =
        BigDecimal("20"),
    )
}
