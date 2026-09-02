package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.application.dto.VeiculoRequest
import br.com.fiap.oficina.application.dto.VeiculoResponse
import br.com.fiap.oficina.application.mapper.VeiculoMapper
import br.com.fiap.oficina.domain.usecase.veiculo.AtualizarVeiculoUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.BuscarVeiculoPorIdUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.BuscarVeiculoPorPlacaUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.BuscarVeiculosPorMotoristaUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.CriarVeiculoUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.ListarVeiculosUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.RemoverVeiculoUseCase
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service

@Service
class VeiculoService(
    private val criarVeiculoUseCase: CriarVeiculoUseCase,
    private val listarVeiculosUseCase: ListarVeiculosUseCase,
    private val buscarVeiculoPorIdUseCase: BuscarVeiculoPorIdUseCase,
    private val buscarVeiculoPorPlacaUseCase: BuscarVeiculoPorPlacaUseCase,
    private val buscarVeiculosPorMotoristaUseCase: BuscarVeiculosPorMotoristaUseCase,
    private val atualizarVeiculoUseCase: AtualizarVeiculoUseCase,
    private val removerVeiculoUseCase: RemoverVeiculoUseCase,
    private val mapper: VeiculoMapper,
) {
    fun criar(request: VeiculoRequest): VeiculoResponse {
        val veiculo = mapper.toDomain(request)
        val response = criarVeiculoUseCase.executar(veiculo)
        return mapper.toResponse(response)
    }

    fun listarTodos(): List<VeiculoResponse> = listarVeiculosUseCase.executar().map { mapper.toResponse(it) }

    fun buscarPorId(id: String): VeiculoResponse {
        val resultado = buscarVeiculoPorIdUseCase.executar(Id.fromString(id))
        return mapper.toResponse(resultado)
    }

    fun buscarPorPlaca(placa: String): VeiculoResponse {
        val resultado = buscarVeiculoPorPlacaUseCase.executar(placa)
        return mapper.toResponse(resultado)
    }

    fun buscarPorMotorista(motoristaId: String): List<VeiculoResponse> =
        buscarVeiculosPorMotoristaUseCase.executar(Id.fromString(motoristaId)).map { mapper.toResponse(it) }

    fun atualizar(id: String, request: VeiculoRequest): VeiculoResponse {
        val veiculo = mapper.toDomain(request.copy(id = id))
        val response = atualizarVeiculoUseCase.executar(veiculo)
        return mapper.toResponse(response)
    }

    fun remover(id: String) = removerVeiculoUseCase.executar(Id.fromString(id))
}
