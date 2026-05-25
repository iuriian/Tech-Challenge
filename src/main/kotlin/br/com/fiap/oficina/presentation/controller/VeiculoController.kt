package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.VeiculoService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.presentation.dto.VeiculoDTO
import br.com.fiap.oficina.presentation.mapper.VeiculoMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/v1/veiculos")
class VeiculoController(
    private val service: VeiculoService,
    private val mapper: VeiculoMapper
) {
    @PostMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Criar um novo veiculo", description = "Cadastra um novo veiculo no sistema")
    fun criar(@Valid @RequestBody veiculo: VeiculoDTO): VeiculoDTO {
        val entity = this.mapper.toEntity(veiculo)
        return mapper.toResponse(service.salvarVeiculo(entity))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar veiculo por ID", description = "Busca um veiculo pelo seu ID único")
    fun buscarVeiculoPorId(
        @Parameter(description = "Id do Veiculo", required = true, example = "1")
        @PathVariable idVeiculo: Long
    ): VeiculoDTO? {
        return service.buscarPorId(idVeiculo)?.let { mapper.toResponse(it) }
    }

    @GetMapping("/placa/{placa}")
    @Operation(summary = "Buscar veiculo pela sua Placa", description = "Buscar veiculo por sua sua placa")
    fun buscarVeiculoPorPlaca(
        @Parameter(description = "Placa do Veiculo", required = true, example = "abc1234")
        @PathVariable placa: String
    ): VeiculoDTO? {
        return service.buscarPorPlaca(placa)?.let { mapper.toResponse(it) }
    }

    @GetMapping("/motorista/{motorista}")
    @Operation(summary = "Buscar veiculos por motorista", description = "Buscar veiculos de um motorista cadastrado")
    fun buscarVeiculosPorMotorista(
        @Parameter(description = "Motorista do veiculo", required = true, example = "João Silva")
        @PathVariable motorista: Cliente
    ): List<VeiculoDTO?> {
        return service.buscarPorMotorista(motorista).map { mapper.toResponse(it) }
    }

}