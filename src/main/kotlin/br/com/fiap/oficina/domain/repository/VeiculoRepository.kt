package br.com.fiap.oficina.domain.repository

import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.valueobject.Id

interface VeiculoRepository {

    fun salvar(veiculo: Veiculo): Veiculo

    fun buscarPorId(id: Id): Veiculo?

    fun buscarPorPlaca(placa: String): Veiculo?

    fun buscarPorMotorista(motoristaId: Id): List<Veiculo>

    fun existePorPlaca(placa: String): Boolean
}
