package br.com.fiap.oficina.presentation.servico.exception

import br.com.fiap.oficina.domain.exception.ServicoNaoEncontradoException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ServicoExceptionHandler {
    @ExceptionHandler(ServicoNaoEncontradoException::class)
    fun handleServicoNaoEncontrado(exception: ServicoNaoEncontradoException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            exception.message ?: "Serviço não encontrado",
        )
}
