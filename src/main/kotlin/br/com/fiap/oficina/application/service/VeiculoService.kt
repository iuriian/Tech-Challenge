package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service

@Service
class VeiculoService(
    private val repository: VeiculoRepository
) {
    fun salvarVeiculo(veiculo: Veiculo): Veiculo {
        require(!repository.existePorPlaca(veiculo.placa)) { "Veiculo já cadastrado" }
        return repository.salvar(veiculo)
    }

    fun buscarPorId(id: Id): Veiculo? {
        return repository.buscarPorId(id)
    }

    fun buscarPorPlaca(placa: String): Veiculo? {
        return repository.buscarPorPlaca(placa)
    }

    fun buscarPorMotorista(motorista: Cliente): List<Veiculo> {
        return repository.buscarPorMotorista(motorista)
    }
}
