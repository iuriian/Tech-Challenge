package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.usecase.veiculo.AtualizarVeiculoUseCase
import br.com.fiap.oficina.application.usecase.veiculo.BuscarVeiculoPorIdUseCase
import br.com.fiap.oficina.application.usecase.veiculo.BuscarVeiculoPorPlacaUseCase
import br.com.fiap.oficina.application.usecase.veiculo.BuscarVeiculosPorMotoristaUseCase
import br.com.fiap.oficina.application.usecase.veiculo.CriarVeiculoUseCase
import br.com.fiap.oficina.application.usecase.veiculo.ListarVeiculosUseCase
import br.com.fiap.oficina.application.usecase.veiculo.RemoverVeiculoUseCase
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

@RestController
@RequestMapping("/veiculos")
@Tag(name = "Veículos", description = "Operações relacionadas ao gerenciamento de veículos")
class VeiculoController(
    private val criarVeiculoUseCase: CriarVeiculoUseCase,
    private val buscarVeiculoPorIdUseCase: BuscarVeiculoPorIdUseCase,
    private val buscarVeiculoPorPlacaUseCase: BuscarVeiculoPorPlacaUseCase,
    private val buscarVeiculosPorMotoristaUseCase: BuscarVeiculosPorMotoristaUseCase,
    private val listarVeiculosUseCase: ListarVeiculosUseCase,
    private val atualizarVeiculoUseCase: AtualizarVeiculoUseCase,
    private val removerVeiculoUseCase: RemoverVeiculoUseCase,
    private val mapper: VeiculoMapper,
) {
    @PostMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar um novo veículo", description = "Cadastra um novo veículo no sistema")
    fun criar(
        @Valid @RequestBody dto: VeiculoDTO,
    ): VeiculoDTO =
        try {
            mapper.toResponse(criarVeiculoUseCase.executar(mapper.toEntity(dto)))
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.CONFLICT, e.message, e)
        }

    @GetMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Buscar veículo por ID", description = "Busca um veículo pelo seu ID único")
    fun buscarVeiculoPorId(
        @Parameter(description = "ID do veículo", required = true)
        @PathVariable id: String,
    ): VeiculoDTO? = buscarVeiculoPorIdUseCase.executar(Id.fromString(id))?.let { mapper.toResponse(it) }

    @GetMapping("/placa/{placa}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Buscar veículo pela placa", description = "Busca um veículo pela sua placa")
    fun buscarVeiculoPorPlaca(
        @Parameter(description = "Placa do veículo", required = true, example = "abc1234")
        @PathVariable placa: String,
    ): VeiculoDTO? = buscarVeiculoPorPlacaUseCase.executar(placa)?.let { mapper.toResponse(it) }

    @GetMapping("/motorista/{motoristaId}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Buscar veículos por motorista", description = "Busca veículos cadastrados para um cliente")
    fun buscarVeiculosPorMotorista(
        @Parameter(description = "ID do cliente (motorista)", required = true)
        @PathVariable motoristaId: String,
    ): List<VeiculoDTO> =
        buscarVeiculosPorMotoristaUseCase.executar(Id.fromString(motoristaId)).map { mapper.toResponse(it) }

    @GetMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Listar veículos", description = "Lista todos os veículos cadastrados no sistema")
    fun listarTodos(): List<VeiculoDTO> = listarVeiculosUseCase.executar().map { mapper.toResponse(it) }

    @PutMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Atualizar um veículo", description = "Atualiza os dados de um veículo existente")
    fun atualizar(
        @Parameter(description = "ID do veículo a ser atualizado", required = true)
        @PathVariable id: String,
        @Valid @RequestBody dto: VeiculoDTO,
    ): VeiculoDTO =
        try {
            mapper.toResponse(atualizarVeiculoUseCase.executar(mapper.toEntityComId(id, dto)))
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.CONFLICT, e.message, e)
        }

    @DeleteMapping("/{id}")
    @RolesAllowed("ADMIN")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover um veículo", description = "Exclui um veículo do sistema pelo seu ID")
    fun remover(
        @Parameter(description = "ID do veículo a ser removido", required = true)
        @PathVariable id: String,
    ) {
        try {
            removerVeiculoUseCase.executar(Id.fromString(id))
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message, e)
        }
    }
}
