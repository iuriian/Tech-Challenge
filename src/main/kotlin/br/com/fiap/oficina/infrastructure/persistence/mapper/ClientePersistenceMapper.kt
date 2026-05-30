package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Contato
import br.com.fiap.oficina.domain.entity.Endereco
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.infrastructure.persistence.entity.ClienteJpaEntity
import br.com.fiap.oficina.infrastructure.persistence.entity.ContatoJpaEntity
import br.com.fiap.oficina.infrastructure.persistence.entity.DocumentoEmbeddable
import br.com.fiap.oficina.infrastructure.persistence.entity.EnderecoJpaEntity
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget

@Mapper(componentModel = "spring")
abstract class ClientePersistenceMapper {

    abstract fun toDomain(entity: ClienteJpaEntity): Cliente

    abstract fun toJpa(domain: Cliente): ClienteJpaEntity

    // O lado "cliente" das associações é religado em @AfterMapping para evitar
    // a recursão infinita do mapeamento bidirecional.
    @Mapping(target = "cliente", ignore = true)
    abstract fun toEndereco(entity: EnderecoJpaEntity): Endereco

    @Mapping(target = "cliente", ignore = true)
    abstract fun toEnderecoJpa(domain: Endereco): EnderecoJpaEntity

    @Mapping(target = "cliente", ignore = true)
    abstract fun toContato(entity: ContatoJpaEntity): Contato

    @Mapping(target = "cliente", ignore = true)
    abstract fun toContatoJpa(domain: Contato): ContatoJpaEntity

    abstract fun toDocumento(embeddable: DocumentoEmbeddable): Documento

    abstract fun toDocumentoEmbeddable(documento: Documento): DocumentoEmbeddable

    @AfterMapping
    protected fun linkDomainBackReferences(@MappingTarget cliente: Cliente) {
        cliente.endereco?.cliente = cliente
        cliente.contatos.forEach { it.cliente = cliente }
    }

    @AfterMapping
    protected fun linkJpaBackReferences(@MappingTarget entity: ClienteJpaEntity) {
        entity.endereco?.cliente = entity
        entity.contatos.forEach { it.cliente = entity }
    }
}
