package br.com.fiap.oficina.presentation.exception

import br.com.fiap.oficina.domain.exception.PecaNaoEncontradoException
import br.com.fiap.oficina.presentation.controller.PecaController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice(assignableTypes = [PecaController::class])
class PecaExceptionHandler {
    @ExceptionHandler(PecaNaoEncontradoException::class)
    @ResponseBody
    fun handlePecaNaoEncontrada(ex: PecaNaoEncontradoException): ResponseEntity<Map<String, String>> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapOf("message" to (ex.message ?: "Peça não encontrada")))

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseBody
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        val status =
            if (ex.message == "Peça já cadastrada") {
                HttpStatus.CONFLICT
            } else {
                HttpStatus.BAD_REQUEST
            }
        return ResponseEntity
            .status(status)
            .body(mapOf("message" to (ex.message ?: "Requisição inválida")))
    }
}
