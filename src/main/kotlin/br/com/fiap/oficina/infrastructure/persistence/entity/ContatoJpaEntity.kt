package br.com.fiap.oficina.infrastructure.persistence.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "contatos")
class ContatoJpaEntity {

    @Id
    lateinit var id: UUID

    @Column(nullable = false)
    lateinit var tipo: String

    @Column(nullable = false)
    lateinit var nome: String

    @Column(nullable = false)
    lateinit var telefone: String

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    var cliente: ClienteJpaEntity? = null
}
