package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service

data class VeiculoComando(
    val marca: String,
    val nome: String,
    val modelo: String,
    val ano: String,
    val placa: String,
    val motoristaId: Id,
)

@Service
class VeiculoService(
    private val repository: VeiculoRepository,
    private val clienteRepository: ClienteRepository,
) {
    fun salvarVeiculo(comando: VeiculoComando): Veiculo {
        require(!repository.existePorPlaca(comando.placa)) { "Veiculo já cadastrado" }

        val motorista =
            clienteRepository.buscarPorId(comando.motoristaId)
                ?: throw IllegalArgumentException("Cliente não encontrado com o ID: ${comando.motoristaId}")

        return repository.salvar(
            Veiculo.criar(
                marca = comando.marca,
                nome = comando.nome,
                modelo = comando.modelo,
                ano = comando.ano,
                placa = comando.placa,
                motorista = motorista,
            ),
        )
    }

    fun atualizarVeiculo(
        id: Id,
        comando: VeiculoComando,
    ): Veiculo {
        val existente =
            repository.buscarPorId(id)
                ?: throw IllegalArgumentException("Veículo não encontrado com o ID: $id")

        if (existente.placa != comando.placa) {
            require(!repository.existePorPlaca(comando.placa)) {
                "Já existe um veículo cadastrado com a placa: ${comando.placa}"
            }
        }

        val motorista =
            clienteRepository.buscarPorId(comando.motoristaId)
                ?: throw IllegalArgumentException("Cliente não encontrado com o ID: ${comando.motoristaId}")

        return repository.salvar(
            Veiculo(
                id = existente.id,
                marca = comando.marca,
                nome = comando.nome,
                modelo = comando.modelo,
                ano = comando.ano,
                placa = comando.placa,
                motorista = motorista,
            ),
        )
    }

    fun removerVeiculo(id: Id) {
        repository.buscarPorId(id)
            ?: throw IllegalArgumentException("Veículo não encontrado com o ID: $id")
        repository.remover(id)
    }

    fun buscarPorId(id: Id): Veiculo? = repository.buscarPorId(id)

    fun buscarPorPlaca(placa: String): Veiculo? = repository.buscarPorPlaca(placa)

    fun buscarPorMotorista(motoristaId: Id): List<Veiculo> = repository.buscarPorMotorista(motoristaId)

    fun listarTodos(): List<Veiculo> = repository.listarTodos()
}
