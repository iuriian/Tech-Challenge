package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.infrastructure.persistence.entity.Cliente
import br.com.fiap.oficina.infrastructure.persistence.entity.Veiculo
import org.springframework.data.jpa.repository.JpaRepository

interface VeiculoRepository: JpaRepository<Veiculo, Long> {
    fun findByPlaca(placa: String): Veiculo?

    fun findByMotorista(motorista: Cliente): List<Veiculo>

    fun findByIdVeiculo(idVeiculo: Long): Veiculo?

    fun existsByPlaca(placa: String): Boolean
}