package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.domain.usecase.funcionario.AtualizarFuncionarioUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.BuscarFuncionarioPorIdUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.BuscarFuncionarioPorNomeUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.CriarFuncionarioUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.ListarFuncionariosUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.RemoverFuncionarioUseCase
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.dto.FuncionarioDto
import br.com.fiap.oficina.presentation.mapper.FuncionarioMapper
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
    private val criarFuncionarioUseCase: CriarFuncionarioUseCase,
    private val listarFuncionariosUseCase: ListarFuncionariosUseCase,
    private val buscarFuncionarioPorIdUseCase: BuscarFuncionarioPorIdUseCase,
    private val buscarFuncionarioPorNomeUseCase: BuscarFuncionarioPorNomeUseCase,
    private val atualizarFuncionarioUseCase: AtualizarFuncionarioUseCase,
    private val removerFuncionarioUseCase: RemoverFuncionarioUseCase,
    private val mapper: FuncionarioMapper,
) {
    @PostMapping
    @Operation(summary = "Criar um novo funcionário", description = "Cadastra um novo funcionário no sistema")
    fun cadastrar(
        @Valid @RequestBody funcionarioDto: FuncionarioDto,
    ): FuncionarioDto {
        val entity = mapper.toEntity(funcionarioDto)
        return mapper.toResponse(criarFuncionarioUseCase.executar(entity))
    }

    @GetMapping
    @Operation(
        summary = "ListarListar todos os funcionários",
        description = "Retorna uma lista com todos os funcionários cadastrados",
    )
    fun listarTodos(): List<FuncionarioDto> =
        listarFuncionariosUseCase.executar().map { mapper.toResponse(it) }

    @GetMapping("/id/{id}")
    @Operation(
        summary = "Buscar funcionário por ID",
        description = "Busca um funcionário através do seu identificador único",
    )
    fun buscarPorId(
        @PathVariable id: String,
    ): FuncionarioDto? = buscarFuncionarioPorIdUseCase.executar(Id.fromString(id))?.let { mapper.toResponse(it) }

    @GetMapping("/nome/{nome}")
    @Operation(summary = "Buscar funcionário por nome", description = "Busca um funcionário através do seu nome")
    fun buscarPorNome(
        @Parameter(description = "Nome do funcionário", required = true, example = "Vini Jr.")
        @PathVariable nome: String,
    ): FuncionarioDto? = buscarFuncionarioPorNomeUseCase.executar(nome)?.let { mapper.toResponse(it) }

    @PutMapping("/{id}")
    @Operation(
        summary = "Alterar dados de um funcionário",
        description = "Atualiza as informações de um funcionário existente",
    )
    fun alterar(
        @PathVariable id: String,
        @Valid @RequestBody funcionarioDto: FuncionarioDto,
    ): FuncionarioDto {
        val entity = mapper.toEntityComId(id, funcionarioDto)
        return mapper.toResponse(atualizarFuncionarioUseCase.executar(entity))
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deletar um funcionário",
        description = "Remove um funcionário do sistema através do seu identificador único",
    )
    fun deletar(
        @PathVariable id: String,
    ) = removerFuncionarioUseCase.executar(Id.fromString(id))
}
