package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Contato
import br.com.fiap.oficina.domain.entity.Endereco
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.entity.ClienteJpaEntity
import br.com.fiap.oficina.infrastructure.persistence.entity.ContatoJpaEntity
import br.com.fiap.oficina.infrastructure.persistence.entity.DocumentoEmbeddable
import br.com.fiap.oficina.infrastructure.persistence.entity.EnderecoJpaEntity
import org.springframework.stereotype.Component

@Component
class ClientePersistenceMapper {
    fun toDomain(entity: ClienteJpaEntity): Cliente =
        Cliente(
            id = Id(entity.id),
            nome = entity.nome,
            documento = toDocumentoDomain(entity.documento),
            email = entity.email,
            endereco = entity.endereco?.let(::toEnderecoDomain),
            contatos = entity.contatos.map(::toContatoDomain),
        )

    fun toJpa(domain: Cliente): ClienteJpaEntity {
        val entity =
            ClienteJpaEntity().apply {
                id = domain.id.valor
                nome = domain.nome
                documento = toDocumentoEmbeddable(domain.documento)
                email = domain.email
                endereco = domain.endereco?.let(::toEnderecoJpa)
                contatos = domain.contatos.map(::toContatoJpa).toMutableList()
            }
        // Religa o lado "cliente" das associações para manter o mapeamento bidirecional.
        entity.endereco?.cliente = entity
        entity.contatos.forEach { it.cliente = entity }
        return entity
    }

    fun toEnderecoDomain(entity: EnderecoJpaEntity): Endereco =
        Endereco(
            id = Id(entity.id),
            logradouro = entity.logradouro,
            numero = entity.numero,
            complemento = entity.complemento,
            bairro = entity.bairro,
            cidade = entity.cidade,
            estado = entity.estado,
            cep = entity.cep,
        )

    fun toEnderecoJpa(domain: Endereco): EnderecoJpaEntity =
        EnderecoJpaEntity().apply {
            id = domain.id.valor
            logradouro = domain.logradouro
            numero = domain.numero
            complemento = domain.complemento
            bairro = domain.bairro
            cidade = domain.cidade
            estado = domain.estado
            cep = domain.cep
        }

    fun toContatoDomain(entity: ContatoJpaEntity): Contato =
        Contato(
            id = Id(entity.id),
            tipo = entity.tipo,
            nome = entity.nome,
            telefone = entity.telefone,
        )

    fun toContatoJpa(domain: Contato): ContatoJpaEntity =
        ContatoJpaEntity().apply {
            id = domain.id.valor
            tipo = domain.tipo
            nome = domain.nome
            telefone = domain.telefone
        }

    private fun toDocumentoDomain(embeddable: DocumentoEmbeddable): Documento =
        Documento(embeddable.numero, embeddable.tipoPessoa)

    private fun toDocumentoEmbeddable(documento: Documento): DocumentoEmbeddable =
        DocumentoEmbeddable().apply {
            numero = documento.numero
            tipoPessoa = documento.tipoPessoa
        }
}
