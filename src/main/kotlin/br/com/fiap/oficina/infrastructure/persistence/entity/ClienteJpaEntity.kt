package br.com.fiap.oficina.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "clientes")
class ClienteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

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
