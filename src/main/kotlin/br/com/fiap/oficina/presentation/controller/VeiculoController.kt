package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.service.VeiculoService
import br.com.fiap.oficina.presentation.dto.VeiculoDTO
import br.com.fiap.oficina.presentation.mapper.VeiculoMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.annotation.security.RolesAllowed
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
@RequestMapping("/veiculos")
@Tag(name = "Veículos", description = "Operações relacionadas ao gerenciamento de veículos")
class VeiculoController(private val veiculoService: VeiculoService, private val mapper: VeiculoMapper) {
    @PostMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Criar um novo veículo", description = "Cadastra um novo veículo no sistema")
    fun criar(@Valid @RequestBody dto: VeiculoDTO): ResponseEntity<VeiculoDTO> {
        val request = mapper.toCriarRequest(dto)
        val response = mapper.toDto(veiculoService.criar(request))
        val location =
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id)
                .toUri()
        return ResponseEntity.created(location).body(response)
    }

    @GetMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Buscar veículo por ID", description = "Busca um veículo pelo seu ID único")
    fun buscarVeiculoPorId(
        @Parameter(description = "ID do veículo", required = true)
        @PathVariable id: String,
    ): VeiculoDTO = mapper.toDto(veiculoService.buscarPorId(id))

    @GetMapping("/placa/{placa}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Buscar veículo pela placa", description = "Busca um veículo pela sua placa")
    fun buscarVeiculoPorPlaca(
        @Parameter(description = "Placa do veículo", required = true, example = "abc1234")
        @PathVariable placa: String,
    ): VeiculoDTO = mapper.toDto(veiculoService.buscarPorPlaca(placa))

    @GetMapping("/motorista/{motoristaId}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Buscar veículos por motorista", description = "Busca veículos cadastrados para um cliente")
    fun buscarVeiculosPorMotorista(
        @Parameter(description = "ID do cliente (motorista)", required = true)
        @PathVariable motoristaId: String,
    ): List<VeiculoDTO> = veiculoService.buscarPorMotorista(motoristaId).map { mapper.toDto(it) }

    @GetMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Listar veículos", description = "Lista todos os veículos cadastrados no sistema")
    fun listarTodos(): List<VeiculoDTO> = veiculoService.listarTodos().map { mapper.toDto(it) }

    @PutMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Atualizar um veículo", description = "Atualiza os dados de um veículo existente")
    fun atualizar(
        @Parameter(description = "ID do veículo a ser atualizado", required = true)
        @PathVariable id: String,
        @Valid @RequestBody dto: VeiculoDTO,
    ): VeiculoDTO {
        val request = mapper.toAtualizarRequest(dto)
        return mapper.toDto(veiculoService.atualizar(id, request))
    }

    @DeleteMapping("/{id}")
    @RolesAllowed("ADMIN")
    @Operation(summary = "Remover um veículo", description = "Exclui um veículo do sistema pelo seu ID")
    fun remover(
        @Parameter(description = "ID do veículo a ser removido", required = true)
        @PathVariable id: String,
    ) {
        veiculoService.remover(id)
    }
}
