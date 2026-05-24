package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.application.ServicoService;
import br.com.fiap.oficina.presentation.dto.ServicoDto
import br.com.fiap.oficina.presentation.mapper.ServicoMapper;
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/servicos")
public class ServicoController(
    private val service:ServicoService,
    private val mapper:ServicoMapper,
) {

    @PostMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Criar um novo servico",
        description = "Cadastra um novo servico no sistema"
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun criar(
        @Valid
        @RequestBody
        dto: ServicoDto
    ): ServicoDto {
        val entity = mapper.toEntity(dto)
        val saved = service.salvar(entity)

        return mapper.toResponse(saved)
    }

    @PutMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Atualizar um servico",
        description = "Atualiza um servico no sistema"
    )
    fun atualizar(
        @Valid
        @RequestBody
        dto: ServicoDto
    ): ServicoDto {
        val entity = mapper.toEntity(dto)
        val saved = service.salvar(entity)

        return mapper.toResponse(saved)
    }

    @GetMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Listar servico por ID",
        description = "Lista um servico do sistema pelo ID"
    )
    fun listarPorId(
        @Parameter(
            description = "ID do serviço",
            required = true,
            example = "1")
        @PathVariable id: Long
    ):ServicoDto? {
        return service.listarPorId(id)?.let{
                    mapper.toResponse(it)
                }
    }

    @GetMapping()
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Listar servicos",
        description = "Lista todos os servicos"
    )
    fun listarTodos(): List<ServicoDto> {
        return service.listarTodos().map{
            mapper.toResponse(it)
        }
    }

    @DeleteMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Deletar servico por ID",
        description = "Deleta um servico do sistema pelo ID"
    )
    fun deletarPorId(){}

}
