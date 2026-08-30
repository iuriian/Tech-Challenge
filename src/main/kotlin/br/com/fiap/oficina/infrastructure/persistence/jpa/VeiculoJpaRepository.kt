package br.com.fiap.oficina.infrastructure.persistence.jpa

import br.com.fiap.oficina.infrastructure.persistence.entity.VeiculoJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface VeiculoJpaRepository : JpaRepository<VeiculoJpaEntity, UUID> {
    fun findByPlaca(placa: String): VeiculoJpaEntity?

    fun findByMotoristaId(id: UUID): List<VeiculoJpaEntity>

    fun findByIdVeiculo(idVeiculo: UUID): VeiculoJpaEntity?

    fun existsByPlaca(placa: String): Boolean
}
