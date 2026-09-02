package br.com.fiap.oficina.presentation.exception

import br.com.fiap.oficina.domain.exception.FuncionarioNaoEncontradoException
import br.com.fiap.oficina.presentation.controller.FuncionarioController
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice(assignableTypes = [FuncionarioController::class])
class FuncionarioExceptionHandler {
    @ExceptionHandler(FuncionarioNaoEncontradoException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun handleFuncionarioNaoEncontrado(ex: FuncionarioNaoEncontradoException): Map<String, String> =
        mapOf("message" to (ex.message ?: "Funcionário não encontrado"))
}
