package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.service.FuncionarioService
import br.com.fiap.oficina.presentation.dto.FuncionarioDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/funcionarios")
@Tag(name = "Funcionários", description = "Operações relacionadas ao gerenciamento de funcionários")
class FuncionarioController(
    private val service: FuncionarioService,
) {
    @PostMapping
    @Operation(summary = "Criar um novo funcionário", description = "Cadastra um novo funcionário no sistema")
    fun cadastrar(
        @Valid @RequestBody funcionarioDto: FuncionarioDto,
    ): FuncionarioDto = service.cadastrar(funcionarioDto)

    @GetMapping
    @Operation(summary = "ListarListar todos os funcionários",
               description = "Retorna uma lista com todos os funcionários cadastrados")
    fun listarTodos(): List<FuncionarioDto> = service.listarTodos()

    @GetMapping("/id/{id}")
    @Operation(summary = "Buscar funcionário por ID",
               description = "Busca um funcionário através do seu identificador único")
    fun buscarPorId(
        @PathVariable id: String,
    ): FuncionarioDto? = service.buscarPorId(id)

    @GetMapping("/nome/{nome}")
    @Operation(summary = "Buscar funcionário por nome", description = "Busca um funcionário através do seu nome")
    fun buscarPorNome(
        @Parameter(description = "Nome do funcionário", required = true, example = "Vini Jr.")
        @PathVariable nome: String,
    ): FuncionarioDto? = service.buscarPorNome(nome)

    @PutMapping("/{id}")
    @Operation(summary = "Alterar dados de um funcionário",
               description = "Atualiza as informações de um funcionário existente")
    fun alterar(
        @PathVariable id: String,
        @Valid @RequestBody funcionarioDto: FuncionarioDto,
    ): FuncionarioDto = service.editar(id, funcionarioDto)

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um funcionário",
               description = "Remove um funcionário do sistema através do seu identificador único")
    fun deletar(
        @PathVariable id: String,
    ) = service.deletar(id)
}
