package br.com.fiap.oficina.presentation.exception

import br.com.fiap.oficina.domain.exception.ClienteNaoEncontradoException
import br.com.fiap.oficina.presentation.controller.ClienteController
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice(assignableTypes = [ClienteController::class])
class ClienteExceptionHandler {
    @ExceptionHandler(ClienteNaoEncontradoException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun handleClienteNaoEncontrado(ex: ClienteNaoEncontradoException): Map<String, String> =
        mapOf("message" to (ex.message ?: "Cliente não encontrado"))
}
