package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.dto.FuncionarioRequest
import br.com.fiap.oficina.application.dto.FuncionarioResponse
import br.com.fiap.oficina.application.service.FuncionarioService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
@RequestMapping("/funcionarios")
@Tag(name = "Funcionários", description = "Operações relacionadas ao gerenciamento de funcionários")
class FuncionarioController(private val funcionarioService: FuncionarioService) {
    @PostMapping
    @Operation(summary = "Criar um novo funcionário", description = "Cadastra um novo funcionário no sistema")
    fun cadastrar(@Valid @RequestBody request: FuncionarioRequest): ResponseEntity<FuncionarioResponse> {
        val response = funcionarioService.cadastrar(request)
        val location =
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/id/{id}")
                .buildAndExpand(response.id)
                .toUri()
        return ResponseEntity.created(location).body(response)
    }

    @GetMapping
    @Operation(
        summary = "Listar todos os funcionários",
        description = "Retorna uma lista com todos os funcionários cadastrados",
    )
    fun listarTodos(): List<FuncionarioResponse> = funcionarioService.listarTodos()

    @GetMapping("/id/{id}")
    @Operation(
        summary = "Buscar funcionário por ID",
        description = "Busca um funcionário através do seu identificador único",
    )
    fun buscarPorId(@PathVariable id: String): FuncionarioResponse = funcionarioService.buscarPorId(id)

    @GetMapping("/nome/{nome}")
    @Operation(summary = "Buscar funcionário por nome", description = "Busca um funcionário através do seu nome")
    fun buscarPorNome(
        @Parameter(description = "Nome do funcionário", required = true, example = "Vini Jr.")
        @PathVariable nome: String,
    ): FuncionarioResponse = funcionarioService.buscarPorNome(nome)

    @PutMapping("/{id}")
    @Operation(
        summary = "Alterar dados de um funcionário",
        description = "Atualiza as informações de um funcionário existente",
    )
    fun alterar(@PathVariable id: String, @Valid @RequestBody request: FuncionarioRequest): FuncionarioResponse =
        funcionarioService.editar(id, request)

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deletar um funcionário",
        description = "Remove um funcionário do sistema através do seu identificador único",
    )
    fun deletar(@PathVariable id: String) = funcionarioService.deletar(id)
}
