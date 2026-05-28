package br.com.fiap.oficina.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "clientes")
class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    lateinit var nome: String

    @Column(nullable = false, unique = true)
    lateinit var documento: Documento

    @Column(nullable = false, unique = true)
    lateinit var email: String

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    var endereco: Endereco? = null

    @OneToMany(mappedBy = "cliente", cascade = [CascadeType.ALL], orphanRemoval = true)
    var contatos: MutableList<Contato> = mutableListOf()
}