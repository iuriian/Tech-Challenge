package br.com.fiap.oficina.presentation.exception

import br.com.fiap.oficina.domain.exception.ClienteNaoEncontradoException
import br.com.fiap.oficina.domain.exception.VeiculoNaoEncontradoException
import br.com.fiap.oficina.presentation.controller.VeiculoController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice(assignableTypes = [VeiculoController::class])
class VeiculoExceptionHandler {
    @ExceptionHandler(VeiculoNaoEncontradoException::class, ClienteNaoEncontradoException::class)
    @ResponseBody
    fun handleNotFound(ex: RuntimeException): ResponseEntity<Map<String, String>> = ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(mapOf("message" to (ex.message ?: "Recurso não encontrado")))

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseBody
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        val status =
            if (ex.message?.contains("cadastrado", ignoreCase = true) == true ||
                ex.message?.contains("Já existe", ignoreCase = true) == true
            ) {
                HttpStatus.CONFLICT
            } else {
                HttpStatus.BAD_REQUEST
            }
        return ResponseEntity
            .status(status)
            .body(mapOf("message" to (ex.message ?: "Requisição inválida")))
    }
}
