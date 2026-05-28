package br.com.fiap.oficina.domain.repository

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface VeiculoRepository: JpaRepository<Veiculo, Long> {
    fun findByPlaca(placa: String): Veiculo?

    fun findByMotorista(motorista: Cliente): List<Veiculo>

    fun findByIdVeiculo(idVeiculo: Long): Veiculo?

    fun existsByPlaca(placa: String): Boolean
}