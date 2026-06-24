package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.infrastructure.persistence.entity.ServicoJpaEntity
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget

// ServicoStatus is in package domain.enum — "enum" is a Java keyword, so kapt cannot
// include getStatus()/setStatus() in the Java stub bodies for Servico or ServicoJpaEntity.
// MapStruct therefore cannot wire status automatically; @AfterMapping handles it in Kotlin,
// which resolves the property via Kotlin metadata rather than the Java stub.
@Mapper(componentModel = "spring", uses = [ClientePersistenceMapper::class, VeiculoPersistenceMapper::class, PecaPersistenceMapper::class])
abstract class ServicoPersistenceMapper {

    abstract fun toDomain(entity: ServicoJpaEntity): Servico

    abstract fun toJpa(domain: Servico): ServicoJpaEntity

    @AfterMapping
    protected fun mapStatusToDomain(entity: ServicoJpaEntity, @MappingTarget domain: Servico) {
        domain.status = entity.status
    }

    @AfterMapping
    protected fun mapStatusToJpa(domain: Servico, @MappingTarget jpa: ServicoJpaEntity) {
        jpa.status = domain.status
    }
}
