package br.com.fiap.oficina.domain.repository

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo

interface VeiculoRepository {

    fun salvar(veiculo: Veiculo): Veiculo

    fun buscarPorId(idVeiculo: Long): Veiculo?

    fun buscarPorPlaca(placa: String): Veiculo?

    fun buscarPorMotorista(motorista: Cliente): List<Veiculo>

    fun existePorPlaca(placa: String): Boolean
}
