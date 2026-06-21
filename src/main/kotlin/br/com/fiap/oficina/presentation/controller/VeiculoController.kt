package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.service.VeiculoComando
import br.com.fiap.oficina.application.service.VeiculoService
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.dto.VeiculoDTO
import br.com.fiap.oficina.presentation.mapper.VeiculoMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@RestController
@RequestMapping("/veiculos")
@Tag(name = "Veículos", description = "Operações relacionadas ao gerenciamento de veículos")
class VeiculoController(
    private val service: VeiculoService,
    private val mapper: VeiculoMapper
) {

    @PostMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar um novo veículo", description = "Cadastra um novo veículo no sistema")
    fun criar(@Valid @RequestBody dto: VeiculoDTO): VeiculoDTO {
        return try {
            mapper.toResponse(
                service.salvarVeiculo(
                    VeiculoComando(
                        marca = dto.marca,
                        nome = dto.nome,
                        modelo = dto.modelo,
                        ano = dto.ano,
                        placa = dto.placa,
                        motoristaId = Id.from(dto.motoristaId)
                    )
                )
            )
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.CONFLICT, e.message, e)
        }
    }

    @GetMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Buscar veículo por ID", description = "Busca um veículo pelo seu ID único")
    fun buscarVeiculoPorId(
        @Parameter(description = "ID do veículo", required = true)
        @PathVariable id: UUID
    ): VeiculoDTO? {
        return service.buscarPorId(Id.from(id))?.let { mapper.toResponse(it) }
    }

    @GetMapping("/placa/{placa}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Buscar veículo pela placa", description = "Busca um veículo pela sua placa")
    fun buscarVeiculoPorPlaca(
        @Parameter(description = "Placa do veículo", required = true, example = "abc1234")
        @PathVariable placa: String
    ): VeiculoDTO? {
        return service.buscarPorPlaca(placa)?.let { mapper.toResponse(it) }
    }

    @GetMapping("/motorista/{motoristaId}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Buscar veículos por motorista", description = "Busca veículos cadastrados para um cliente")
    fun buscarVeiculosPorMotorista(
        @Parameter(description = "ID do cliente (motorista)", required = true)
        @PathVariable motoristaId: UUID
    ): List<VeiculoDTO> {
        return service.buscarPorMotorista(Id.from(motoristaId)).map { mapper.toResponse(it) }
    }
}
