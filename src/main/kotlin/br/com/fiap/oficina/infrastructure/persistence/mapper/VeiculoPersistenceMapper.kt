package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.infrastructure.persistence.entity.VeiculoJpaEntity
import org.mapstruct.Mapper

@Mapper(componentModel = "spring", uses = [ClientePersistenceMapper::class])
interface VeiculoPersistenceMapper {

    fun toDomain(entity: VeiculoJpaEntity): Veiculo

    fun toJpa(domain: Veiculo): VeiculoJpaEntity
}
