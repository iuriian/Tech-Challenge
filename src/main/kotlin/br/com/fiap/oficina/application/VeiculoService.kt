package br.com.fiap.oficina.application

import br.com.fiap.oficina.infrastructure.persistence.entity.Cliente
import br.com.fiap.oficina.infrastructure.persistence.entity.Veiculo
import br.com.fiap.oficina.infrastructure.persistence.repository.VeiculoRepository
import br.com.fiap.oficina.presentation.mapper.VeiculoMapper
import org.springframework.stereotype.Service

@Service
class VeiculoService(
    private val repository: VeiculoRepository,
    private val mapper: VeiculoMapper
) {
    fun salvarVeiculo(veiculo: Veiculo): Veiculo {
        if (repository.existsByPlaca(veiculo.placa)){
            throw IllegalArgumentException("Veiculo já cadastrado")
        }
        return repository.save(veiculo)
    }

    fun buscarPorId(idVeiculo: Long): Veiculo? {
        return repository.findByIdVeiculo(idVeiculo)
    }

    fun buscarPorPlaca(placa: String): Veiculo? {
        return repository.findByPlaca(placa)
    }

    fun buscarPorMotorista(motorista: Cliente): List<Veiculo> {
        return repository.findByMotorista(motorista)
    }
}