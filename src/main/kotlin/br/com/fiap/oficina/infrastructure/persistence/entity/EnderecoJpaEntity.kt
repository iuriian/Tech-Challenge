package br.com.fiap.oficina.infrastructure.persistence.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "enderecos")
class EnderecoJpaEntity {

    @Id
    lateinit var id: UUID

    @Column(nullable = false)
    lateinit var logradouro: String

    @Column(nullable = false)
    lateinit var numero: String

    @Column(nullable = true)
    var complemento: String? = null

    @Column(nullable = false)
    lateinit var bairro: String

    @Column(nullable = false)
    lateinit var cidade: String

    @Column(nullable = false)
    lateinit var estado: String

    @Column(nullable = false)
    lateinit var cep: String

    @OneToOne(mappedBy = "endereco")
    var cliente: ClienteJpaEntity? = null
}
