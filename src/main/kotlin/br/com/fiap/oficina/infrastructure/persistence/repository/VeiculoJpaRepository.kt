package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.infrastructure.persistence.entity.ClienteJpaEntity
import br.com.fiap.oficina.infrastructure.persistence.entity.VeiculoJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface VeiculoJpaRepository : JpaRepository<VeiculoJpaEntity, Long> {
    fun findByPlaca(placa: String): VeiculoJpaEntity?

    fun findByMotorista(motorista: ClienteJpaEntity): List<VeiculoJpaEntity>

    fun findByIdVeiculo(idVeiculo: Long): VeiculoJpaEntity?

    fun existsByPlaca(placa: String): Boolean
}
