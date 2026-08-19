package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.port.`in`.CriarServicoCommand
import br.com.fiap.oficina.application.port.`in`.CriarServicoUseCase
import br.com.fiap.oficina.presentation.dto.CriarServicoRequest
import br.com.fiap.oficina.presentation.dto.ServicoResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/catalogo/servicos")
@Tag(
    name = "Catálogo de Serviços",
    description = "Operações relacionadas ao catálogo de serviços da oficina",
)
class ServicoCatalogoController(
    private val criarServicoUseCase: CriarServicoUseCase,
) {
    @PostMapping
    @RolesAllowed("ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Criar serviço de catálogo",
        description = "Cadastra um novo serviço oferecido pela oficina",
    )
    fun criar(
        @Valid
        @RequestBody
        request: CriarServicoRequest,
    ): ServicoResponse {
        val servico =
            criarServicoUseCase.executar(
                CriarServicoCommand(
                    descricao = request.descricao,
                    valor = request.valor,
                ),
            )

        return ServicoResponse(
            id = servico.id.valor,
            descricao = servico.descricao,
            valor = servico.valor,
        )
    }
}
