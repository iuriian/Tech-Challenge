package br.com.fiap.oficina.infrastructure.persistence.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "clientes")
class ClienteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID = UUID.randomUUID()

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
