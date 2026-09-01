package br.com.fiap.oficina.infrastructure.persistence.jpa.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "clientes")
class ClienteJpaEntity {
    @Id
    lateinit var id: UUID

    @Column(nullable = false)
    lateinit var nome: String

    @Embedded
    lateinit var documento: DocumentoEmbeddable

    @Column(nullable = false, unique = true)
    lateinit var email: String

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    var endereco: EnderecoJpaEntity? = null

    @OneToMany(mappedBy = "cliente", cascade = [CascadeType.ALL], orphanRemoval = true)
    var contatos: MutableList<ContatoJpaEntity> = mutableListOf()
}
