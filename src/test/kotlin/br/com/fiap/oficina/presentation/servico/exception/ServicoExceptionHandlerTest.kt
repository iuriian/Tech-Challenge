package br.com.fiap.oficina.presentation.servico.exception

import br.com.fiap.oficina.domain.exception.ServicoNaoEncontradoException
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ServicoExceptionHandlerTest {
    private val handler = ServicoExceptionHandler()

    @Test
    fun `deve traduzir servico nao encontrado para 404`() {
        val exception =
            ServicoNaoEncontradoException(
                Id.generate(),
            )

        val problemDetail =
            handler.handleServicoNaoEncontrado(exception)

        assertEquals(
            HttpStatus.NOT_FOUND.value(),
            problemDetail.status,
        )
        assertEquals(
            exception.message,
            problemDetail.detail,
        )
    }
}
