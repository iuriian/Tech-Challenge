package br.com.fiap.oficina.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "contatos")
class Contato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    lateinit var tipo: String

    @Column(nullable = false)
    lateinit var nome: String

    @Column(nullable = false)
    lateinit var telefone: String

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    var cliente: Cliente? = null
}
