package br.com.fiap.oficina.domain.usecase.veiculo

import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.exception.ClienteNaoEncontradoException
import br.com.fiap.oficina.domain.exception.VeiculoNaoEncontradoException
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.VeiculoRepository

class AtualizarVeiculoUseCase(
    private val veiculoRepository: VeiculoRepository,
    private val clienteRepository: ClienteRepository,
) {
    fun executar(veiculo: Veiculo): Veiculo {
        val existente =
            veiculoRepository.buscarPorId(veiculo.id)
                ?: throw VeiculoNaoEncontradoException.porId(veiculo.id.valor.toString())

        if (existente.placa != veiculo.placa) {
            require(!veiculoRepository.existePorPlaca(veiculo.placa)) {
                "Já existe um veículo cadastrado com a placa: ${veiculo.placa}"
            }
        }

        val motorista =
            clienteRepository.buscarPorId(veiculo.motorista.id)
                ?: throw ClienteNaoEncontradoException.porId(veiculo.motorista.id.valor.toString())

        return veiculoRepository.salvar(
            Veiculo(
                id = existente.id,
                marca = veiculo.marca,
                nome = veiculo.nome,
                modelo = veiculo.modelo,
                ano = veiculo.ano,
                placa = veiculo.placa,
                motorista = motorista,
            ),
        )
    }
}
